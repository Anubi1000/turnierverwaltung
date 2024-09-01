package de.anubi1000.turnierverwaltung.server

import androidx.compose.ui.res.useResource
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
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
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Single
import org.mongodb.kbson.ObjectId
import java.io.InputStream

@Single
class Server(private val tournamentRepository: TournamentRepository) {
    private val messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 3)
    private var currentTournamentId: ObjectId? = null

    private val server = embeddedServer(Netty, port = 8080) {
        installStatusPages()
        installWebSockets()

        routing {
            staticResources("/", "scoreboard")

            webSocket("/ws") {
                val clientId = hashCode()
                log.info("Client with id $clientId connected")

                currentTournamentId?.let { id ->
                    log.info("Sending current tournament to newly connected client")
                    val tournament = tournamentRepository.getTournamentById(id) ?: return@let
                    sendSerialized(tournament.toSetTournamentMessage())
                }

                val job = launch {
                    messageFlow.asSharedFlow()
                        .collect { message ->
                            log.debug { "Sending message of type ${message.javaClass.name} to client with id $clientId" }
                            sendSerialized(message)
                        }
                }

                runCatching {
                    incoming.consumeEach { frame ->
                        if (frame !is Frame.Text) return@consumeEach

                        val data = frame.readText()
                        val json = Json.parseToJsonElement(data).jsonObject
                        val type = json["type"]!!.jsonPrimitive.content
                        when (type) {
                            "resend_tournament" -> {
                                val tournament = tournamentRepository.getTournamentById(currentTournamentId!!) ?: return@consumeEach
                                sendSerialized(tournament.toSetTournamentMessage())
                            }
                        }
                    }
                }

                // Wait for websocket close
                closeReason.await()
                log.info("Client with id $clientId disconnected")

                job.cancel()
            }
        }
    }

    fun setCurrentTournament(tournament: Tournament) {
        log.debug { "Changing tournament for scoreboard. id=${tournament.id.toHexString()}" }
        currentTournamentId = tournament.id
        // TODO: Add handling when value was not emitted
        messageFlow.tryEmit(tournament.toSetTournamentMessage())
    }

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop()
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
                status = HttpStatusCode.NotFound
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
