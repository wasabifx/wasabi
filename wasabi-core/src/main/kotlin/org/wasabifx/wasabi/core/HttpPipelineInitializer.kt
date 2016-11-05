package org.wasabifx.wasabi.core

/*
class HttpPipelineInitializer(val appServer: AppServer) : SimpleChannelInboundHandler<HttpMessage>() {

    private val logger = LoggerFactory.getLogger(HttpPipelineInitializer::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        // If we get here no connection upgrade was requested
        logger.debug("" + msg!!.protocolVersion() + " connection")

        // Increment the retain count due to our pipeline setup, if we don't it gets released during
        // WebSocket handshake or post pipeline flush.
        val fullMessage = msg as FullHttpRequest
        fullMessage.retain()
        println("Channel read")

        when (msg.protocolVersion().text()) {
            "HTTP/2.0" -> initHttp2Pipeline(ctx, msg)
            "HTTP/1.1", "HTTP/1.0" -> initHttpPipeline(ctx, msg)
            else -> {
                throw IllegalStateException("unknown protocol: " + msg.protocolVersion().text())
            }
        }
    }

    private fun initHttp2Pipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        logger.debug("Initialising HTTP/2 Pipeline")
        val pipeline = ctx!!.pipeline()
        pipeline.addLast("http2", Http2HandlerBuilder(appServer).build())
        ctx.executor()
    }

    private fun initHttpPipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        logger.debug("Initialising HTTP Pipeline")
        if (msg!!.headers().get(HttpHeaders.Names.UPGRADE) == "websocket") {
            applyWebSocketPipeline(ctx, msg)
        } else {
            applyHttp1Pipeline(ctx, msg)
        }
    }

    private fun applyWebSocketPipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        val fullMessage = msg as FullHttpRequest
        // TODO handle the channel not found exception gracefully...
        val path = fullMessage.uri
        val channel = PatternMatchingChannelLocator(appServer.channels).findChannelHandler(path)
        val pipeline = ctx!!.pipeline()
        val context = pipeline.context(this)
        pipeline.addLast(WebSocketProtocolHandler(path, null, true))
        pipeline.addLast(WebSocketFrameHandler(channel.handler))
        context.fireChannelRead(msg)
    }

    private fun applyHttp1Pipeline(ctx: ChannelHandlerContext?, msg: HttpMessage?) {
        val pipeline = ctx!!.pipeline()
        val context = pipeline.context(this)
        pipeline.addAfter(context.name(), "chunkedWriter", ChunkedWriteHandler())
        pipeline.addAfter("chunkedWriter", "http1", HttpRequestHandler(appServer))
        context.fireChannelRead(msg)
    }
}*/
