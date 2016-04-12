package org.wasabi.protocol.http2

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http2.*
import org.slf4j.LoggerFactory

class Http2Handler(decoder: Http2ConnectionDecoder, encoder: Http2ConnectionEncoder, settings: Http2Settings) : Http2ConnectionHandler(decoder, encoder, settings), Http2FrameListener {

    private val logger = LoggerFactory.getLogger(Http2Handler::class.java)

    init {
        logger.debug("HTTP2Handler created...")
    }

    override fun onPingRead(ctx: ChannelHandlerContext?, data: ByteBuf?) {
        throw UnsupportedOperationException()
    }

    override fun onDataRead(ctx: ChannelHandlerContext?, streamId: Int, data: ByteBuf?, padding: Int, endOfStream: Boolean): Int {
        throw UnsupportedOperationException()
    }

    override fun onSettingsRead(ctx: ChannelHandlerContext?, settings: Http2Settings?) {
        throw UnsupportedOperationException()
    }

    override fun onUnknownFrame(ctx: ChannelHandlerContext?, frameType: Byte, streamId: Int, flags: Http2Flags?, payload: ByteBuf?) {
        throw UnsupportedOperationException()
    }

    override fun onHeadersRead(ctx: ChannelHandlerContext?, streamId: Int, headers: Http2Headers?, padding: Int, endOfStream: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun onHeadersRead(ctx: ChannelHandlerContext?, streamId: Int, headers: Http2Headers?, streamDependency: Int, weight: Short, exclusive: Boolean, padding: Int, endOfStream: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun onPushPromiseRead(ctx: ChannelHandlerContext?, streamId: Int, promisedStreamId: Int, headers: Http2Headers?, padding: Int) {
        throw UnsupportedOperationException()
    }

    override fun onPingAckRead(ctx: ChannelHandlerContext?, data: ByteBuf?) {
        throw UnsupportedOperationException()
    }

    override fun onRstStreamRead(ctx: ChannelHandlerContext?, streamId: Int, errorCode: Long) {
        throw UnsupportedOperationException()
    }

    override fun onPriorityRead(ctx: ChannelHandlerContext?, streamId: Int, streamDependency: Int, weight: Short, exclusive: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun onGoAwayRead(ctx: ChannelHandlerContext?, lastStreamId: Int, errorCode: Long, debugData: ByteBuf?) {
        throw UnsupportedOperationException()
    }

    override fun onWindowUpdateRead(ctx: ChannelHandlerContext?, streamId: Int, windowSizeIncrement: Int) {
        throw UnsupportedOperationException()
    }

    override fun onSettingsAckRead(ctx: ChannelHandlerContext?) {
        throw UnsupportedOperationException()
    }
}