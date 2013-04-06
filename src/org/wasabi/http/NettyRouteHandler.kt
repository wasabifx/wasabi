package org.wasabi.http

import io.netty.channel.ChannelInboundMessageHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import org.wasabi.routing.Routes
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
import org.wasabi.routing.MethodNotAllowedException
import org.wasabi.routing.RouteNotFoundException
import org.wasabi.routing.RouteHandler
import io.netty.handler.codec.http.DefaultHttpResponse

public class NettyRouteHandler(private val routes: Routes): ChannelInboundMessageHandlerAdapter<Any>() {
    var request: Request? = null

    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {
        // just a prototype...

        if (msg is HttpRequest) {
            request = Request(msg)
        }

        if (msg is HttpContent) {
            if (msg is LastHttpContent) {
                try {
                    val handler = routes.findRouteHandler(request?.method, request?.uri)
                    val h : RouteHandler.() -> Unit = handler!!
                    val rh = RouteHandler(request!!, Response(ctx!!))
                    rh.h()
                    ctx.flush()?.addListener(ChannelFutureListener.CLOSE)
                } catch (e: MethodNotAllowedException) {
                    setStatusCode(ctx!!, 405, "Method not allowed")
                    // TODO: Add Allow header with methods allowed
                } catch (e: RouteNotFoundException) {
                    setStatusCode(ctx!!, 404, "Not found")
                }


            }
        }


    }
    public override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        setStatusCode(ctx!!, 500, cause?.getMessage())
    }

    fun setStatusCode(ctx: ChannelHandlerContext, statusCode: Int, text: String) {
        var response = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(statusCode,text))
        ctx.nextOutboundMessageBuffer()?.add(response)
        ctx.flush()?.addListener(ChannelFutureListener.CLOSE)
    }

    fun setHeader(ctx: ChannelHandlerContext) {

    }

    fun sendResponse(ctx: ChannelHandlerContext) {
        ctx.flush()?.addListener(ChannelFutureListener.CLOSE)
    }


}

public class OutboundResponse() {

    public var statusCode: Int = 200
    public var statusText: String = ""

}
