package org.wasabi.protocol.http

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpMessage
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.stream.ChunkedWriteHandler
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer

class HttpPipelineInitializer(val appServer: AppServer) : SimpleChannelInboundHandler<HttpMessage>() {

    private val logger = LoggerFactory.getLogger(HttpPipelineInitializer::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        // If we get here no connection upgrade was requested, go HTTP1
        logger.debug("Direct " + msg!!.protocolVersion() + " connection (no upgrade was attempted)");
        val pipeline = ctx!!.pipeline();
        val context = pipeline.context(this);
        pipeline.addLast("decoder", HttpRequestDecoder())
        pipeline.addLast("encoder", HttpResponseEncoder())
        pipeline.addAfter(context.name(), "chunkedWriter", ChunkedWriteHandler());
        pipeline.addAfter("chunkedWriter", "http1", NettyRequestHandler(appServer));
        pipeline.replace(this, "aggregator", HttpObjectAggregator(1048576))
        ctx.fireChannelRead(msg);
    }
}