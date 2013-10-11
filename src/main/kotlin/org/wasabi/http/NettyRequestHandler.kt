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
import org.wasabi.deserializers.Deserializer
import java.nio.ByteBuffer
import org.slf4j.LoggerFactory


// TODO: This class needs cleaning up
public class NettyRequestHandler(private val appServer: AppServer, routeLocator: RouteLocator): ChannelInboundMessageHandlerAdapter<Any>(), RouteLocator by routeLocator {

    var request: Request? = null
    var body = ""
    val response = Response()
    var chunkedTransfer = false
    var decoder : HttpPostRequestDecoder? = null
    val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
    val preParsingInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreParsing }
    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val postSerializationInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostSerialization }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }
    var deserializer : Deserializer? = null
    private var log = LoggerFactory.getLogger(javaClass<NettyRequestHandler>())


    public override fun messageReceived(ctx: ChannelHandlerContext?, msg: Any?) {

        if (msg is HttpRequest) {
            request = Request(msg)
            request!!.accept.mapTo(response.requestedContentTypes, { it.key })

            if (request!!.method == HttpMethod.POST || request!!.method == HttpMethod.PUT || request!!.method == HttpMethod.PATCH) {
                deserializer = appServer.deserializers.find { it.canDeserialize(request!!.contentType)}
                // TODO: Re-do this as it's now considering special case for multi-part
                if (request!!.contentType.contains("application/x-www-form-urlencoded") || request!!.contentType.contains("multipart/form-data")) {
                    decoder = HttpPostRequestDecoder(factory, msg)
                    chunkedTransfer = request!!.chunked
                }
            }


        }

        if (msg is HttpContent) {

            log!!.debug("About to preparse")

            runInterceptors(preParsingInterceptors)

            if (deserializer != null) {
                // TODO: Add support for chunked transfer
                deserializeBody(msg)
            }
            if (msg is LastHttpContent) {
                try {

                    var continueRequest : Boolean

                    // process all interceptors that are global
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
                    // Run global interceptors again
                    continueRequest = runInterceptors(postRequestInterceptors)

                } catch (e: InvalidMethodException)  {
                    response.setAllowedMethods(e.allowedMethods)
                    response.setStatus(StatusCodes.MethodNotAllowed)
                } catch (e: RouteNotFoundException) {
                    response.setStatus(StatusCodes.NotFound)
                } catch (e: Exception) {
                    log!!.debug("Exception during web invocation: ${e.getMessage()}")
                    response.setStatus(StatusCodes.InternalServerError)
                }
                writeResponse(ctx!!, response)
            }
        }


    }

    private fun runInterceptors(interceptors: List<InterceptorEntry>, route: Route? = null): Boolean {
        var interceptorsToRun : List<InterceptorEntry>
        if (route == null) {
            interceptorsToRun = interceptors.filter { it.path == "*" }
        } else {
            interceptorsToRun = interceptors.filter { compareRouteSegments(route, it.path)}
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
        // TODO: Implement once you have CORS properly supported
        response.addExtraHeader("Access-Control-Request-Method", "GET, POST, PUT")
        response.addExtraHeader("Access-Control-Allow-Origin", "*")
        response.addExtraHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
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
            // TODO: Make this a stream
            var buffer = ""
            if (response.sendBuffer is String) {
                if (response.sendBuffer != "") {
                    buffer = (response.sendBuffer as String)
                } else {
                    buffer = response.statusDescription
                }
            } else {
                if (response.negotiatedMediaType != "") {
                    val serializer = appServer.serializers.find { it.canSerialize(response.negotiatedMediaType) }
                    if (serializer != null) {
                        buffer = serializer.serialize(response.sendBuffer!!)
                    } else {
                        response.setStatus(StatusCodes.UnsupportedMediaType)
                    }
                }
            }
            httpResponse = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(response.statusCode,response.statusDescription),  Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8))

            try {
                // process all interceptors that are post serialization
                runInterceptors(postSerializationInterceptors)
            }
            catch(e: Exception)
            {
               response.setStatus(StatusCodes.InternalServerError)
                // TODO Should we flush the body contents here?
            }

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

    public override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        // TODO: Log actual message
        response.setStatus(StatusCodes.InternalServerError)
        writeResponse(ctx!!, response)
    }


    private fun deserializeBody(msg: HttpContent) {
        // TODO: Re-structure all this and fix it as it requires a special case for decoder
        if (decoder != null) {
            decoder!!.offer(msg)
            request!!.bodyParams.putAll(deserializer!!.deserialize(decoder!!.getBodyHttpDatas()!!))
        } else {
            // TODO: Add support for CharSet
            request!!.bodyParams.putAll(deserializer!!.deserialize(msg.data()?.array()?.toString("UTF-8")!!))
        }
    }


}

