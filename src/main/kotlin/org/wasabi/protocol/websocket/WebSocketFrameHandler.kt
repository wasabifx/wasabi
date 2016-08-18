package org.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import org.slf4j.LoggerFactory
import org.wasabi.websocket.ChannelHandler

class WebSocketFrameHandler: SimpleChannelInboundHandler<WebSocketFrame>() {

    private var log = LoggerFactory.getLogger(WebSocketFrameHandler::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: WebSocketFrame?) {
        log.info(msg.toString())

        // Grab the handler for the current channel.
        //val handler = channelLocator.findChannelHandler(handshaker!!.uri().toString()).handler
        //val channelExtension : ChannelHandler.() -> Unit = handler
        //val channelHandler = ChannelHandler(ctx, frame)
        //channelHandler.channelExtension()
    }
}