package org.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.routing.PatternMatchingChannelLocator
import org.wasabi.websocket.ChannelHandler

/**
 * Created by condaa1 on 9/04/16.
 */
class WebSocketHandler(val appServer: AppServer, val handshaker: WebSocketServerHandshaker?) : SimpleChannelInboundHandler<Any?>() {

    private val log = LoggerFactory.getLogger(WebSocketHandler::class.java)

    // TODO make configurable
    private var channelLocator = PatternMatchingChannelLocator(appServer.channels)

    init {
        log.info("WebSocketHandler created.")
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {
        // throw UnsupportedOperationException()
        log.info("Websocket channel read....")

        val frame = msg as WebSocketFrame
        // Check for closing websocket frame
        // TODO this should probably be allowed to be handled in handler for cleanup etc.
        if (frame is CloseWebSocketFrame)
        {
            handshaker!!.close(ctx!!.channel(), frame.retain())
        }

        // Grab the handler for the current channel.
        val handler = channelLocator.findChannelHandler(handshaker!!.uri().toString()).handler
        val channelExtension : ChannelHandler.() -> Unit = handler
        val channelHandler = ChannelHandler(ctx, frame)
        channelHandler.channelExtension()

        // TODO look to hook write here.... for post interceptors.
    }
}