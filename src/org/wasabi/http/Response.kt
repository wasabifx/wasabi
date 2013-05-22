package org.wasabi.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.CharsetUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInboundMessageHandlerAdapter


public class Response() {

    // TODO: Clean all this up. Internal headers
    public var etag: String = ""
    public var location: String = ""
    public var allow: String = ""
    public var buffer: String = ""
    public var contentType: String = ""

    public var statusCode: Int = 200
    public var statusDescription: String = ""


    fun send(message: String) {
        buffer = message
    }

    public fun setStatusCode(statusCode: Int, statusDescription: String) {
        this.statusCode = statusCode
        this.statusDescription = statusDescription
    }

    public fun setContentType(contentType: String) {
        this.contentType = contentType
    }

    public fun setContentType(contentType: ContentType) {
        setContentType(contentType.toContentTypeString())
    }

    public fun setHeader(name: String, value: String) {
        // TODO:
    }


}
