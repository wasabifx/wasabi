package org.wasabi.protocol.http2

import io.netty.handler.codec.http2.*
import io.netty.handler.logging.LogLevel.INFO

/**
 * Created by condaa1 on 8/04/16.
 */
class Http2HandlerBuilder : AbstractHttp2ConnectionHandlerBuilder<Http2Handler, Http2HandlerBuilder>() {

    private val logger : Http2FrameLogger = Http2FrameLogger(INFO, Http2Handler::class.java);

    init {
        frameLogger(logger)
    }

    public override fun build(): Http2Handler? {
        return super.build()
    }

    public override fun build(decoder: Http2ConnectionDecoder?, encoder: Http2ConnectionEncoder?, initialSettings: Http2Settings?): Http2Handler? {
        val handler = Http2Handler(decoder!!, encoder!!, initialSettings!!)
        frameListener(handler)
        return handler
    }

}