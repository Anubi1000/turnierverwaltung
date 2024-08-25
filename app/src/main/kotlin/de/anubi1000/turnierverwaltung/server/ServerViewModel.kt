package de.anubi1000.turnierverwaltung.server

import androidx.compose.ui.res.useResource
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.mongodb.kbson.ObjectId
import java.io.InputStream

class ServerViewModel(private val tournamentRepository: TournamentRepository) {
    private val messageFlow = MutableSharedFlow<Message>()
    private var currentTournamentId: ObjectId? = null

    private val server = embeddedServer(Netty, port = 8080) {
        installStatusPages()
        installWebSockets()

        routing {
            staticResources("/", "scoreboard")

            webSocket("/ws") {
                currentTournamentId?.let { id ->
                    val tournament = tournamentRepository.getTournamentById(id) ?: return@let
                    sendSerialized(tournament.toSetTournamentMessage())
                }

                val job = launch {
                    messageFlow.asSharedFlow()
                        .collect { message ->
                            sendSerialized(message)
                        }
                }

                // Wait for websocket close
                closeReason.await()

                job.cancel()
            }
        }
    }


    fun start() {
        server.start()
    }

    fun stop() {
        server.stop()
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
