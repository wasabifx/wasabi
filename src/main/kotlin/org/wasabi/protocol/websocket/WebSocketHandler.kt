package org.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer

/**
 * Created by condaa1 on 9/04/16.
 */
class WebSocketHandler(val appServer: AppServer, val handshaker: WebSocketServerHandshaker?) : SimpleChannelInboundHandler<Any?>() {

    private val log = LoggerFactory.getLogger(WebSocketHandler::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {
        // throw UnsupportedOperationException()
        log.info("Websocket channel read....")
    }
}