package org.wasabi.core

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*
import io.netty.handler.stream.ChunkedWriteHandler
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.NettyRequestHandler
import org.wasabi.protocol.http2.Http2HandlerBuilder

class HttpPipelineInitializer(val appServer: AppServer) : SimpleChannelInboundHandler<HttpMessage>() {

    private val logger = LoggerFactory.getLogger(HttpPipelineInitializer::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        // If we get here no connection upgrade was requested
        logger.info("Direct " + msg!!.protocolVersion() + " connection (no upgrade was attempted)");

        logger.info(msg.protocolVersion().text());

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