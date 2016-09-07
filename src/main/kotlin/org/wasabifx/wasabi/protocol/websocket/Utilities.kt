package org.wasabifx.wasabi.protocol.websocket

import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import java.util.*

val channelClients: HashMap<String, ArrayList<Channel>> = HashMap()

fun broadcast(channel: String, message: WebSocketFrame) {
    val clients = channelClients[channel]
    clients!!.forEach {
        client -> sendMessage(client, message)
    }
    // Release the message as it will not get done as flush is not called in this context.
    message.release()
}

fun respond(channel: Channel, message: WebSocketFrame) {
    sendMessage(channel, message)
    message.release()
}

private fun sendMessage(channel: Channel, message: WebSocketFrame) {
    // Make sure we copy the message otherwise it gets released as soon as flush is called.
    val newMessage = message.copy()
    channel.write(newMessage)
    channel.flush()
}
