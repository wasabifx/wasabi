package org.wasabifx.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler

class WebSocketProtocolHandler(val uri:String?, val sub: String?, val foo: Boolean): WebSocketServerProtocolHandler(uri, sub, foo) {

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        // Add new channel to list of client connections to the channel for broadcast.
        val connections = channelClients[uri]
        connections!!.add(ctx!!.channel())
        super.userEventTriggered(ctx, evt)
    }
}