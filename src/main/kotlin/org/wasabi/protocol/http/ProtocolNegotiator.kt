package org.wasabi.protocol.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.ssl.ApplicationProtocolNames
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler
import io.netty.handler.stream.ChunkedWriteHandler
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.app.configuration
import org.wasabi.protocol.http2.Http2HandlerBuilder

/**
 * This handles setting up the pipeline for SSL connections.
 * @property appServer The preconfigured wasabi application server instance.
 */
class ProtocolNegotiator(val appServer: AppServer) : ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_1_1) {

    private val logger = LoggerFactory.getLogger(ProtocolNegotiator::class.java)

    /**
     * This overrides the default Netty implementation, invokes the appropriate function
     * based on the incoming protocol type, we handle HTTP/1.1 and 1.0 within the same handler.
     */
    override fun configurePipeline(ctx: ChannelHandlerContext?, protocol: String?) {
        when(protocol){
            "h2"-> initHttp2Pipeline(ctx)
            "HTTP/1.1" -> initHttpPipeline(ctx)
            "HTTP/1.0" -> initHttpPipeline(ctx)
            else -> {
                throw IllegalStateException("unknown protocol: " + protocol);
            }
        }
    }

    /**
     * Not much to this yet build and add!
     */
    private fun initHttp2Pipeline(ctx: ChannelHandlerContext?)
    {
        logger.info("initHttp2Pipeline")
        val pipeline = ctx!!.pipeline()
        pipeline.addLast("http2", Http2HandlerBuilder(appServer).build());
    }

    /**
     * Here we setup our basic HTTP1 pipeline.
     * TODO max size should be configuration property.
     */
    private fun initHttpPipeline(ctx: ChannelHandlerContext?)
    {
        logger.info("initHttpPipeline")
        val pipeline = ctx!!.pipeline();
        val context = pipeline.context(this);
        pipeline.addLast("decoder", HttpRequestDecoder())
        pipeline.addLast("encoder", HttpResponseEncoder())
        pipeline.addAfter(context.name(), "chunkedWriter", ChunkedWriteHandler());
        pipeline.addAfter("chunkedWriter", "http1", NettyRequestHandler(appServer));
        pipeline.replace(this, "aggregator", HttpObjectAggregator(configuration!!.maxHttpContentLength))
    }
}