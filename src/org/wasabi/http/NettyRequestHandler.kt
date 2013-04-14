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
import org.wasabi.routing.RouteLocator
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory

public class NettyRequestHandler(routeLocator: RouteLocator, parserLocator: ParserLocator): ChannelInboundMessageHandlerAdapter<Any>(), RouteLocator by routeLocator, ParserLocator by parserLocator {

    var request: Request? = null
    var body = ""
    val response = Response()
    var decoder : HttpPostRequestDecoder? = null
    val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
    var chunkedTransfer = false


    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {
        // just a prototype...

        if (msg is HttpRequest) {
            request = Request(msg)
            request!!.parseQueryParams()

            if (request!!.method == HttpMethod.POST || request!!.method == HttpMethod.PUT) {
                decoder = HttpPostRequestDecoder(factory, msg)
                chunkedTransfer = request!!.chunked
            }

        }

        if (msg is HttpContent) {
            decoder?.offer(msg)
            if (chunkedTransfer) {
                processChunkedContent()
            } else {
                processCompleteContent()
            }

            if (msg is LastHttpContent) {
                try {
                    val route = findRoute(request!!.uri!!.split('?')[0], request!!.method!!)
                    request!!.routeParams = route.params
                    val handlerExtension : RouteHandler.() -> Unit = route!!.handler
                    val routeHandler = RouteHandler(request!!, response)
                    routeHandler.handlerExtension()
               // TODO: Errors need to be delegated to error handlers
                } catch (e: MethodNotAllowedException) {
                    response.setStatusCode(405, "Method not allowed")

                } catch (e: RouteNotFoundException) {
                    response.setStatusCode(404, "Not found")
                }
                response.writeResponse(ctx!!)
            }
        }


    }


    private fun processChunkedContent() {

        // TODO

    }

    private fun processCompleteContent() {

        // TODO
    }



    public override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        response.setStatusCode(500, cause?.getMessage()!!)
        response.writeResponse(ctx!!)
    }



}

