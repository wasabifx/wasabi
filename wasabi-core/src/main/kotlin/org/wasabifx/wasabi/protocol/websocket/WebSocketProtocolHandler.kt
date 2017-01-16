package org.wasabifx.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import org.wasabifx.wasabi.events.connectionInactive

class WebSocketProtocolHandler(val uri:String?, val sub: String?, val allowExtensions: Boolean): WebSocketServerProtocolHandler(uri, sub, allowExtensions) {

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {

        // If handshake is complete add new channel to list of client connections to the channel for broadcast.
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            channelClients[uri]!!.add(ctx!!.channel())
        }

        super.userEventTriggered(ctx, evt)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        val channel = ctx!!.channel()
        channelClients[uri]!!.remove(channel)
        connectionInactive(channel)
        super.channelInactive(ctx)
    }
}