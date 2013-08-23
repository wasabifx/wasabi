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
import org.wasabi.interceptors.Interceptor
import org.wasabi.routing.InterceptOn
import org.wasabi.interceptors.InterceptorEntry
import java.util.ArrayList
import org.wasabi.routing.Route
import io.netty.handler.stream.ChunkedFile
import java.io.RandomAccessFile
import org.wasabi.routing.InvalidMethodException
import org.wasabi.routing.RouteNotFoundException


// TODO: This class needs cleaning up
public class NettyRequestHandler(private val appServer: AppServer, routeLocator: RouteLocator): ChannelInboundMessageHandlerAdapter<Any>(), RouteLocator by routeLocator {

    var request: Request? = null
    var body = ""
    val response = Response()
    var decoder : HttpPostRequestDecoder? = null
    val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
    var chunkedTransfer = false
    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }



    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {

        if (msg is HttpRequest) {
            request = Request(msg)

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
                    // process all interceptors that are global
                    var continueRequest : Boolean

                    continueRequest = runInterceptors(preRequestInterceptors)

                    if (continueRequest) {
                        val routeHandlers = findRouteHandlers(request!!.uri.split('?')[0], request!!.method)
                        request!!.routeParams.putAll(routeHandlers.params)

                        continueRequest = runInterceptors(preRequestInterceptors, routeHandlers)

                        if (continueRequest) {
                            for (handler in routeHandlers.handler) {

                                val handlerExtension : RouteHandler.() -> Unit = handler
                                val routeHandler = RouteHandler(request!!, response)

                                routeHandler.handlerExtension()
                                if (!routeHandler.executeNext) {
                                    break
                                }
                            }
                            runInterceptors(postRequestInterceptors, routeHandlers)

                        }
                    }
                } catch (e: InvalidMethodException)  {
                    response.setAllowedMethods(e.allowedMethods)
                    response.setHttpStatus(HttpStatusCodes.MethodNotAllowed)
                } catch (e: RouteNotFoundException) {
                    response.setHttpStatus(HttpStatusCodes.NotFound)
                } catch (e: Exception) {
                    // TODO: Log actual message
                    response.setHttpStatus(HttpStatusCodes.InternalServerError)
                }
                writeResponse(ctx!!, response)
            }
        }


    }

    private fun runInterceptors(interceptors: List<InterceptorEntry>, route: Route? = null): Boolean {
        var interceptorsToRun : List<InterceptorEntry>
        if (route == null) {
            interceptorsToRun = interceptors.filter { it.path == "*"}
        } else {
            interceptorsToRun = interceptors.filter { it.path == "*" || compareRouteSegments(route, it.path)}
        }
        for (interceptorEntry in interceptorsToRun) {
            val interceptor = interceptorEntry.interceptor
            if (!interceptor.intercept(request!!, response)) {
                return false
            }

        }
        return true
    }



    private fun writeResponse(ctx: ChannelHandlerContext, response: Response) {
        var httpResponse : HttpResponse
        response.setResponseCookies()
        if (response.statusCode / 100 == 4 || response.statusCode / 100 == 5) {
            runInterceptors(errorInterceptors)
        }
        if (response.absolutePathToFileToStream != "") {
            httpResponse = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(response.statusCode, response.statusDescription))
            addResponseHeaders(httpResponse, response)
            ctx.write(httpResponse)

            var raf = RandomAccessFile(response.absolutePathToFileToStream, "r");
            var fileLength = raf.length();

            var writeFuture = ctx.write(ChunkedFile(raf, 0, fileLength, 8192));

            writeFuture!!.addListener(ChannelFutureListener.CLOSE)
        }  else {
            // TODO: This needs finishing otherwise serialization of objects won't work
            var data : String = ""
            if (response.sendBuffer is String) {
                if (response.sendBuffer != "") {
                    data = (response.sendBuffer as String)
                } else {
                    data = response.statusDescription
                }
            }
            httpResponse = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(response.statusCode,response.statusDescription),  Unpooled.copiedBuffer(data, CharsetUtil.UTF_8))
            addResponseHeaders(httpResponse, response)
            ctx.write(httpResponse)
            ctx.flush()?.addListener(ChannelFutureListener.CLOSE)
        }
    }

    private fun addResponseHeaders(httpResponse: HttpResponse, response: Response) {
        if (response.allow != "") {
            httpResponse.headers()?.add("Allow", response.allow)
        }
        for (header in response.extraHeaders) {
            if (header.value != "") {
                httpResponse.headers()?.add(header.key, header.value)
            }
        }
    }
    private fun processChunkedContent() {
        try {
            while (decoder!!.hasNext()) {
                val data = decoder!!.next()
                request?.addBodyParam(data!!)
            }
        } catch (e: EndOfDataDecoderException) {
            // TODO: Handle error here
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
        // TODO: Log actual message
        response.setHttpStatus(HttpStatusCodes.InternalServerError)
        writeResponse(ctx!!, response)
    }



}

