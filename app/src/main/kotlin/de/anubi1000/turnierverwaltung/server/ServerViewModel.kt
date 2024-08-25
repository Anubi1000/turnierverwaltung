package de.anubi1000.turnierverwaltung.server

import androidx.compose.ui.res.useResource
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.InputStream

class ServerViewModel {
    val messageFlow = MutableSharedFlow<Message>()

    private val server = embeddedServer(Netty, port = 8080) {
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

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingPeriodMillis = 30000L
        }

        routing {
            staticResources("/", "scoreboard")

            webSocket("/ws") {
                val job = launch {
                    messageFlow.collect { message ->
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