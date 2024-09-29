package de.anubi1000.turnierverwaltung.server

import androidx.compose.ui.res.useResource
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
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
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Single
import org.mongodb.kbson.ObjectId
import java.io.InputStream
import java.util.UUID

@Single
class Server(private val realm: Realm) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 3)
    private var currentTournamentId: MutableStateFlow<ObjectId?> = MutableStateFlow(null)

    private val ktorServer = embeddedServer(Netty, port = 8080) {
        installStatusPages()
        installWebSockets()

        routing {
            staticResources("/", "scoreboard")

            webSocket("/ws") {
                handleConnection()
            }
        }
    }

    fun start() {
        ktorServer.start()

        var job: Job? = null
        currentTournamentId.onEach { id ->
            job?.cancel()
            if (id == null) return@onEach

            job = coroutineScope.launch {
                handelTournamentUpdates(id)
                handleParticipantUpdates(id)
                handleTeamUpdates(id)
                handleClubUpdates(id)
                handleDisciplineUpdates(id)
                handleTeamDisciplineUpdates(id)
            }
        }.launchIn(coroutineScope)
    }

    fun stop() {
        coroutineScope.cancel()
        ktorServer.stop()
    }

    fun setCurrentTournament(tournamentId: ObjectId) {
        currentTournamentId.value = tournamentId
    }

    private suspend fun DefaultWebSocketServerSession.handleConnection() {
        val clientId = UUID.randomUUID()
        log.debug { "Client with id $clientId connected" }
        sendTournament()

        val job = launch {
            messageFlow.asSharedFlow()
                .collect { message ->
                    log.debug { "Sending message of type ${message.javaClass.name} to $clientId" }
                    sendSerialized(message)
                }
        }

        runCatching {
            incoming.consumeEach { handleFrame(clientId, it) }
        }

        log.debug { "Client with id $clientId disconnected" }
        job.cancel()
    }

    private suspend fun DefaultWebSocketServerSession.handleFrame(clientId: UUID, frame: Frame) {
        if (frame !is Frame.Text) return
        log.debug { "Handling text frame from $clientId" }

        val text = frame.readText()
        val json = Json.parseToJsonElement(text).jsonObject
        val type = json["type"]?.jsonPrimitive?.contentOrNull ?: return
        val messageType = Message.Type.fromString(type)
        log.trace { "Message type is $messageType" }

        if (messageType == Message.Type.RESEND_TOURNAMENT) {
            sendTournament()
        }
    }

    private suspend fun DefaultWebSocketServerSession.sendTournament() {
        currentTournamentId.value?.let { tournamentId ->
            val message = withContext(Dispatchers.IO) {
                realm.queryById<Tournament>(tournamentId)!!.toSetTournamentMessage()
            }
            sendSerialized(message)
        }
    }

    private fun CoroutineScope.handelTournamentUpdates(id: ObjectId) = launch {
        realm.query<Tournament>("_id == $0", id).first().asFlow(
            keyPaths = listOf("name"),
        ).collect { change ->
            log.debug { "Handling tournament update for id(${id.toHexString()})" }
            val obj = change.obj
            if (obj != null) {
                messageFlow.emit(obj.toSetTournamentMessage())
            } else {
                log.warn { "Unsetting tournament because it isn't available anymore" }
                currentTournamentId.value = null
            }
        }
    }

    private fun CoroutineScope.handleParticipantUpdates(id: ObjectId) = launch {
        realm.query<Participant>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            log.debug { "Handle participant updates" }
            if (change.deletions.isNotEmpty() || change.changes.isNotEmpty()) {
                messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
            }
        }
    }

    private fun CoroutineScope.handleTeamUpdates(id: ObjectId) = launch {
        realm.query<Team>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
        }
    }

    private fun CoroutineScope.handleClubUpdates(id: ObjectId) = launch {
        realm.query<Club>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            if (change.changes.isNotEmpty()) {
                messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
            }
        }
    }

    private fun CoroutineScope.handleDisciplineUpdates(id: ObjectId) = launch {
        realm.query<Discipline>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
        }
    }

    private fun CoroutineScope.handleTeamDisciplineUpdates(id: ObjectId) = launch {
        realm.query<TeamDiscipline>("tournament._id == $0", id).asFlow().collect { change ->
            if (change !is UpdatedResults) return@collect

            messageFlow.emit(realm.queryById<Tournament>(id)!!.toSetTournamentMessage())
        }
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
