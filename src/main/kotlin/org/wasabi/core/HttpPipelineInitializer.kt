package org.wasabi.core

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMessage
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.stream.ChunkedWriteHandler
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.HttpRequestHandler
import org.wasabi.protocol.http2.Http2HandlerBuilder
import org.wasabi.protocol.websocket.WebSocketFrameHandler

class HttpPipelineInitializer(val appServer: AppServer) : SimpleChannelInboundHandler<HttpMessage>() {

    private val logger = LoggerFactory.getLogger(HttpPipelineInitializer::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        // If we get here no connection upgrade was requested
        logger.info("Direct " + msg!!.protocolVersion() + " connection (no upgrade was attempted)");

        // Increment the retain count due to our pipeline setup, if we don't it gets released during
        // WebSocket handshake or post pipeline flush.
        val fullMessage = msg as FullHttpRequest
        fullMessage.retain()

        when(msg.protocolVersion().text()) {
            "HTTP/2.0"-> initHttp2Pipeline(ctx, msg)
            "HTTP/1.1" -> initHttpPipeline(ctx, msg)
            "HTTP/1.0" -> initHttpPipeline(ctx, msg)
            else -> {
                throw IllegalStateException("unknown protocol: " + msg.protocolVersion().text());
            }
        }
    }

    private fun initHttp2Pipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?)
    {
        logger.info("initHttp2Pipeline")
        val pipeline = ctx!!.pipeline()
        pipeline.addLast("http2", Http2HandlerBuilder(appServer).build());
        ctx.executor()
    }

    private fun initHttpPipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?)
    {
        logger.info("initHttpPipeline")

        if (msg!!.headers().get(HttpHeaders.Names.UPGRADE) == "websocket") {
            applyWebSocketPipeline(ctx, msg)
        } else {
            applyHttp1Pipeline(ctx, msg)
        }

    }

    private fun applyWebSocketPipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        val pipeline = ctx!!.pipeline();
        val context = pipeline.context(this);
        // TODO check we have a channel handler for url and set below instead of "/ws"
        pipeline.addLast(WebSocketServerProtocolHandler("/ws", null, true));
        pipeline.addLast(WebSocketFrameHandler())
        context.fireChannelRead(msg);
    }

    private fun applyHttp1Pipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        val pipeline = ctx!!.pipeline();
        val context = pipeline.context(this);
        pipeline.addAfter(context.name(), "chunkedWriter", ChunkedWriteHandler());
        pipeline.addAfter("chunkedWriter", "http1", HttpRequestHandler(appServer));
        context.fireChannelRead(msg);
    }
}