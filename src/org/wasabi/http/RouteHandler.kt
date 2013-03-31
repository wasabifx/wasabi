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

public class RouteHandler(private val routes: Routes): ChannelInboundMessageHandlerAdapter<Any>() {
    var handler: ((Request, Response) -> Unit)? = null
    var request: Request? = null

    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {
        // just a prototype...

        if (msg is HttpRequest) {
            request = Request(msg)
            try {
                handler = routes.findHandler(request?.method, request?.uri)
            } catch (e: MethodNotAllowedException) {
                sendResponse(ctx!!, 405, e.message)
                // TODO: Add Allow header with methods allowed
            } catch (e: RouteNotFoundException) {
                sendResponse(ctx!!, 404, e.message)
            }
        }

        if (msg is HttpContent) {
            if (msg is LastHttpContent) {

                handler?.invoke(request!!, Response(ctx!!))

                ctx?.flush()?.addListener(ChannelFutureListener.CLOSE)
            }
        }


    }
    public override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        sendResponse(ctx!!, 500, cause?.getMessage())
    }


}

internal fun sendResponse(ctx: ChannelHandlerContext, statusCode: Int, text: String) {
    var response = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(statusCode,text))
    ctx?.nextOutboundMessageBuffer()?.add(response)
    ctx?.flush()?.addListener(ChannelFutureListener.CLOSE)

}