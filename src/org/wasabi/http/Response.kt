package org.wasabi.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.CharsetUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.DefaultFullHttpResponse


public class Response(private val ctx: ChannelHandlerContext) {

    fun send(message: String) {

        val response = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(200, "Good"), Unpooled.copiedBuffer(message, CharsetUtil.UTF_8))

        ctx?.nextOutboundMessageBuffer()?.add(response)

    }
}
