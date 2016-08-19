package org.wasabifx.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class ChannelHandler(val ctx: ChannelHandlerContext?, val frame: WebSocketFrame)  {

    var executeNext = false

    public fun next() {
        executeNext = true
    }

}

fun channelHandler(f: ChannelHandler.()->Unit) = f
