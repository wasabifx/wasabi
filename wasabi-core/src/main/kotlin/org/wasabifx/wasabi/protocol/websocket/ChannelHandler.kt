package org.wasabifx.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class ChannelHandler(val ctx: ChannelHandlerContext?, val frame: WebSocketFrame, val response: Response)  {

    var executeNext = false

    fun next() {
        executeNext = true
    }

}

fun channelHandler(f: ChannelHandler.()->Unit) = f
