package org.wasabi.protocol.http2

import io.netty.handler.codec.http2.*
import io.netty.handler.logging.LogLevel.INFO
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer

class Http2HandlerBuilder(val appServer: AppServer) : AbstractHttp2ConnectionHandlerBuilder<Http2Handler, Http2HandlerBuilder>() {

    private val frameLogger: Http2FrameLogger = Http2FrameLogger(INFO, Http2Handler::class.java);
    private val logger = LoggerFactory.getLogger(Http2HandlerBuilder::class.java)


    init {
        logger.info("http2 handler builder init")
        frameLogger(frameLogger)
    }

    public override fun build(): Http2Handler? {
        logger.info("noarg build")
        return super.build()
    }

    public override fun build(decoder: Http2ConnectionDecoder?, encoder: Http2ConnectionEncoder?, initialSettings: Http2Settings?): Http2Handler? {
        logger.info("arg build")
        val handler = Http2Handler(appServer, decoder!!, encoder!!, initialSettings!!)
        frameListener(handler)
        return handler
    }

}