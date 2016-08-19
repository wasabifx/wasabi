package org.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import org.slf4j.LoggerFactory
import org.wasabi.websocket.ChannelHandler

class WebSocketFrameHandler(val handler: ChannelHandler.() -> Unit): SimpleChannelInboundHandler<WebSocketFrame>() {

    private var log = LoggerFactory.getLogger(WebSocketFrameHandler::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: WebSocketFrame?) {

        // TODO wrap the frame ala wasabi HttpResponse so we can wrap interceptors into the pipeline and
        // TODO wasabi users don't have to care about the underlying context.

        // Grab the handler for the current channel.
        val channelExtension : ChannelHandler.() -> Unit = handler
        val channelHandler = ChannelHandler(ctx, msg!!)
        channelHandler.channelExtension()
    }
}