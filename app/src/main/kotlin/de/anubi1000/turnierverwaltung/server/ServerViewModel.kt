package de.anubi1000.turnierverwaltung.server

import androidx.compose.ui.res.useResource
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.routing
import java.io.InputStream

class ServerViewModel {
    private val server = embeddedServer(Netty, port = 8080) {
        routing {
            staticResources("/", "scoreboard")
        }

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

    fun start() {
        server.start()
    }
}