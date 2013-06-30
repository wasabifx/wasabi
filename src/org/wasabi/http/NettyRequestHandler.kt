package org.wasabi.http

import io.netty.channel.ChannelInboundMessageHandlerAdapter
import io.netty.channel.ChannelHandlerContext
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
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.NotEnoughDataDecoderException
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException
import java.util.Collections
import io.netty.handler.codec.http.CookieDecoder
import java.util.Collection
import io.netty.handler.codec.http.HttpResponse
import org.wasabi.app.AppServer

public class NettyRequestHandler(private val appServer: AppServer, routeLocator: RouteLocator): ChannelInboundMessageHandlerAdapter<Any>(), RouteLocator by routeLocator {

    var request: Request? = null
    var body = ""
    val response = Response()
    var decoder : HttpPostRequestDecoder? = null
    val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
    var chunkedTransfer = false



    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {

        if (msg is HttpRequest) {
            request = Request(msg)
            request!!.parseQueryParams()
            request!!.parseCookies()

            if (request!!.method == HttpMethod.POST) {
                decoder = HttpPostRequestDecoder(factory, msg)
                chunkedTransfer = request!!.chunked
            }

        }

        if (msg is HttpContent) {
            if (decoder != null) {
                decoder!!.offer(msg)
                if (chunkedTransfer) {
                    processChunkedContent()
                } else {
                    processCompleteContent()
                }
            }
            if (msg is LastHttpContent) {
                try {
                    val route = findRoute(request!!.uri!!.split('?')[0], request!!.method!!)
                    request!!.routeParams = route.params


                    for (handler in route!!.handler) {

                        val handlerExtension : RouteHandler.() -> Unit = handler
                        val routeHandler = RouteHandler(request!!, response)

                        routeHandler.handlerExtension()
                        if (!routeHandler.executeNext) {
                            break
                        }
                    }




/*                    var stop = false
                    for (interceptor in appServer.beforeRequestHandlers) {
                        if (!interceptor.handle(request!!, response)) {
                            stop = true
                            break
                        }
                    }*/
      /*              if (!stop) {
                        routeHandler.handlerExtension()
                        for (interceptor in appServer.afterRequestHandlers) {
                            if (!interceptor.handle(request!!, response)) {
                                break
                            }
                        }
                    }
*/

               // TODO: Errors need to be delegated to error handlers
                } catch (e: MethodNotAllowedException) {
                    response.setAllowedMethods(e.allowedMethods)
                    response.setStatus(405, "Method not allowed")

                } catch (e: RouteNotFoundException) {
                    response.send("Not found")
                    response.setStatus(404, "Not found")
                }
                var stop = false
          /*      for (interceptor in appServer.beforeResponseHandler) {
                    if (!interceptor.handle(request!!, response)) {
                        stop = true
                        break
                    }
                }
             */   if (!stop) {
                    writeResponse(ctx!!, response)
                }
            }
        }


    }


    private fun writeResponse(ctx: ChannelHandlerContext, response: Response) {
        var httpResponse = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(response.statusCode,response.statusDescription),  Unpooled.copiedBuffer(response.buffer, CharsetUtil.UTF_8))
        ctx.nextOutboundMessageBuffer()?.add(httpResponse)
        addResponseHeaders(httpResponse, response)
        ctx.flush()?.addListener(ChannelFutureListener.CLOSE)
    }

    private fun addResponseHeaders(httpResponse: HttpResponse, response: Response) {
        if (response.allow != "") {
            httpResponse.headers()?.add("Allow", response.allow)
        }
        for (header in response.extraHeaders) {
            httpResponse.headers()?.add(header.key, header.value)
        }
    }
    private fun processChunkedContent() {
        try {
            while (decoder!!.hasNext()) {
                val data = decoder!!.next()
                request?.addBodyParam(data!!)
            }
        } catch (e: EndOfDataDecoderException) {

        }
    }

    private fun processCompleteContent() {

        var httpData: MutableList<InterfaceHttpData>?
        try {
            httpData = decoder?.getBodyHttpDatas()
            if (httpData != null) {
                request?.parseBodyParams(httpData!!)
            }
        } catch (e: NotEnoughDataDecoderException) {
            // TODO: Handle error here
        }

    }



    public override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        response.setStatus(500, cause?.getMessage()!!)
        writeResponse(ctx!!, response)
    }



}

