package com.hadihariri.wasabi.http

import io.netty.channel.ChannelInboundMessageHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import com.hadihariri.wasabi.routing.Routes
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.LastHttpContent
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.channel.ChannelFutureListener
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil

public class RequestHandler(private val routes: Routes): ChannelInboundMessageHandlerAdapter<Any>() {
    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg is HttpRequest) {
            val request = msg
            println(msg.getUri())
      //     ctx?.write("Fuck you too!")
      //      ctx?.flush()
        }
        if (msg is HttpContent) {
            val content = msg
            println("content")
            if (msg is LastHttpContent) {
                val last = msg
                println("last")
                val response = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(200, "Good"), Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8))

                ctx?.nextOutboundMessageBuffer()?.add(response)
                ctx?.flush()?.addListener(ChannelFutureListener.CLOSE)
            }
        }


    }


}