package org.wasabifx.wasabi.samples

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.events.connectionInactive
import org.wasabifx.wasabi.protocol.websocket.broadcast
import org.wasabifx.wasabi.protocol.websocket.respond

fun main(args: Array<String>) {

    val server = AppServer(AppConfiguration(enableLogging = false))

    // This is currently fired when a websocket client becomes inactive.
    connectionInactive += { print("Client disconnected!\n")}

    server.channel("/whoop", {

        // Directly responds to the client with "WHOOP!" text frame acknowledgement ( yes not very useful! )
        respond(ctx!!.channel(), TextWebSocketFrame("WHOOP!"))
    })

    server.channel("/broadcast", {
        if(frame is TextWebSocketFrame) {

            // This will broadcast to all clients connected to the current channel
            broadcast("/broadcast", frame)
        }
    })

    server.start()
}
