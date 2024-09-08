package de.anubi1000.turnierverwaltung.server

import androidx.compose.ui.res.useResource
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import de.anubi1000.turnierverwaltung.server.messages.Message
import de.anubi1000.turnierverwaltung.server.messages.toSetTournamentMessage
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Single
import org.mongodb.kbson.ObjectId
import java.io.InputStream

@Single
class Server(private val realm: Realm) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 3)
    private var currentTournamentId: MutableStateFlow<ObjectId?> = MutableStateFlow(null)

    private val server = embeddedServer(Netty, port = 8080) {
        installStatusPages()
        installWebSockets()

        routing {
            staticResources("/", "scoreboard")

            webSocket("/ws") {
                handleConnection()
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.handleConnection() {
        currentTournamentId.value?.let { tournamentId ->
            val message = withContext(Dispatchers.IO) {
                realm.queryById<Tournament>(tournamentId)!!.toSetTournamentMessage()
            }
            sendSerialized(message)
        }

        val job = launch {
            messageFlow.asSharedFlow()
                .collect(this@handleConnection::sendSerialized)
        }

        closeReason.await()
        job.cancel()
    }

    fun start() {
        server.start()

        var job: Job? = null
        currentTournamentId.onEach { id ->
            job?.cancel()
            if (id == null) return@onEach

            job = coroutineScope.launch {
                handelTournamentUpdates(id)
                handleParticipantUpdates(id)
                handleClubUpdates(id)
                handleDisciplineUpdates(id)
            }
        }.launchIn(coroutineScope)
    }

    private fun CoroutineScope.handelTournamentUpdates(id: ObjectId) = launch {
        realm.query<Tournament>("_id == $0", id).first().asFlow(
            keyPaths = listOf("name"),
        ).collect { change ->
            val obj = change.obj
            if (obj != null) {
                messageFlow.emit(obj.toSetTournamentMessage())
            } else {
                currentTournamentId.value = null
            }
        }
    }

    private fun CoroutineScope.handleDisciplineUpdates(id: ObjectId) = launch {
        realm.query<Discipline>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
        }
    }

    private fun CoroutineScope.handleParticipantUpdates(id: ObjectId) = launch {
        realm.query<Participant>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            // TODO: Change to update message
            messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
        }
    }

    private fun CoroutineScope.handleClubUpdates(id: ObjectId) = launch {
        realm.query<Club>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
        }
    }

    fun stop() {
        coroutineScope.cancel()
        server.stop()
    }

    fun setCurrentTournament(tournamentId: ObjectId) {
        currentTournamentId.value = tournamentId
    }

    companion object {
        private val log = logger()
    }
}

private fun Application.installStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondBytes(
                contentType = ContentType.Text.Html,
                status = HttpStatusCode.NotFound,
            ) {
                useResource("scoreboard/404.html", InputStream::readAllBytes)
            }
        }
    }
}

private fun Application.installWebSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriodMillis = 30000L
    }
}
