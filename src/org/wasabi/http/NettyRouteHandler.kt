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
    val response = Response()

    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {
        // just a prototype...

        if (msg is HttpRequest) {
            request = Request(msg)
            request?.parseQueryParams()
        }

        if (msg is HttpContent) {
            if (msg is LastHttpContent) {
                try {
                    val handler = routes.findRouteHandler(request?.method!!, request?.uri!!.split('?')[0])
                    val h : RouteHandler.() -> Unit = handler!!
                    val rh = RouteHandler(request!!, response)
                    rh.h()
                    response.writeResponse(ctx!!)
                } catch (e: MethodNotAllowedException) {
                    response.statusCode = 405
                    response.statusDescription = "Method not allowed"
                    response.allow = e.allowedMethods.makeString(",")
                    response.writeResponse(ctx!!)

                } catch (e: RouteNotFoundException) {
                    response.statusCode = 404
                    response.statusDescription = "Not found"
                    response.writeResponse(ctx!!)
                }


            }
        }


    }

    public override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        response.statusCode = 500
        response.statusDescription = cause?.getMessage()!!
        response.writeResponse(ctx!!)
    }



}

