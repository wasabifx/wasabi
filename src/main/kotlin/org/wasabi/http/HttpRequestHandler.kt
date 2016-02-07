package org.wasabi.http

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.handler.stream.ChunkedNioFile
import io.netty.util.CharsetUtil
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.deserializers.Deserializer
import org.wasabi.interceptors.InterceptorEntry
import org.wasabi.routing.*
import java.io.FileInputStream

/**
 * Created by cnwdaa1 on 14/10/2015.
 */
public class HttpRequestHandler(private val appServer: AppServer){

    var request: Request? = null
    val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val preExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreExecution }
    val postExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostExecution }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }
    var chunkedTransfer = false
    private var bypassPipeline = false
    val response = Response()
    var deserializer : Deserializer? = null
    var decoder : HttpPostRequestDecoder? = null
    private var log = LoggerFactory.getLogger(HttpRequestHandler::class.java)
    // TODO make configurable.
    var routeLocator = PatternAndVerbMatchingRouteLocator(appServer.routes)

    public fun handleRequest(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg is HttpRequest) {
            request = Request(msg)

            request!!.accept.mapTo(response.requestedContentTypes, { it.key })

            if (request!!.method == HttpMethod.POST || request!!.method == HttpMethod.PUT || request!!.method == HttpMethod.PATCH) {
                deserializer = appServer.deserializers.firstOrNull { it.canDeserialize(request!!.contentType) }
                // TODO: Re-do this as it's now considering special case for multi-part
                if (request!!.contentType.contains("application/x-www-form-urlencoded") || request!!.contentType.contains("multipart/form-data")) {
                    decoder = HttpPostRequestDecoder(factory, msg)
                    chunkedTransfer = request!!.chunked
                }
            }
        }

        if (msg is HttpContent) {

            runInterceptors(preRequestInterceptors)

            if (deserializer != null) {
                // TODO: Add support for chunked transfer
                deserializeBody(msg)
            }

            if (msg is LastHttpContent) {
                try {

                    // process all interceptors that are global
                    runInterceptors(preExecutionInterceptors)

                    // Only need to check here to stop RouteNotFoundException
                    if (!bypassPipeline) {

                        val routeHandlers = routeLocator.findRouteHandlers(request!!.uri.split('?')[0], request!!.method)
                        request!!.routeParams.putAll(routeHandlers.params)

                        // process the route specific pre execution interceptors
                        runInterceptors(preExecutionInterceptors, routeHandlers)

                        // Now that the global and route specific preexecution interceptors have run, execute the route handlers
                        runHandlers(routeHandlers)

                        // process the route specific post execution interceptors
                        runInterceptors(postExecutionInterceptors, routeHandlers)

                        // Run global interceptors again
                        runInterceptors(postExecutionInterceptors)

                    }

                } catch (e: InvalidMethodException)  {
                    response.setAllowedMethods(e.allowedMethods)
                    response.setStatus(StatusCodes.MethodNotAllowed)
                } catch (e: RouteNotFoundException) {
                    response.setStatus(StatusCodes.NotFound)
                } catch (e: Exception) {
                    log!!.debug("Exception during web invocation: ${e.message}")
                    response.setStatus(StatusCodes.InternalServerError)
                }
                writeResponse(ctx!!, response)
            }
        }
    }

    private fun runHandlers(routeHandlers : Route)
    {
        // If the flag has been set no-op to allow the response to be flushed as is.
        if (bypassPipeline)
        {
            return
        }
        for (handler in routeHandlers.handler) {

            val handlerExtension : RouteHandler.() -> Unit = handler
            val routeHandler = RouteHandler(request!!, response)

            routeHandler.handlerExtension()
            if (!routeHandler.executeNext) {
                break
            }
        }
    }

    private fun runInterceptors(interceptors: List<InterceptorEntry>, route: Route? = null) {
        // If the flag has been set no-op to allow the response to be flushed as is.
        if (bypassPipeline)
        {
            return
        }
        var interceptorsToRun : List<InterceptorEntry>
        if (route == null) {
            interceptorsToRun = interceptors.filter { it.path == "*" }
        } else {
            interceptorsToRun = interceptors.filter { routeLocator.compareRouteSegments(route, it.path) }
        }
        for (interceptorEntry in interceptorsToRun) {

            val interceptor = interceptorEntry.interceptor
            val executeNext = interceptor.intercept(request!!, response)

            if (!executeNext) {
                bypassPipeline = true
                break
            }
        }
    }

    private fun writeResponse(ctx: ChannelHandlerContext, response: Response) {
        var httpResponse : HttpResponse
        if (response.statusCode / 100 == 4 || response.statusCode / 100 == 5) {
            runInterceptors(errorInterceptors)
        }

        if (response.absolutePathToFileToStream != "") {

            httpResponse = DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus(response.statusCode, response.statusDescription));
            response.setHeaders()
            addResponseHeaders(httpResponse, response)
            ctx.write(httpResponse)

            var fileStream = FileInputStream(response.absolutePathToFileToStream)

            var fileChannel = fileStream.channel

            // NOTE we can probably use DefaultFileRegion here but this allows for data modification on the fly.
            ctx.write(ChunkedNioFile(fileChannel, 8192), ctx.newProgressivePromise())

            // TODO Get rid of this!!
            var lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)

            if (request!!.connection.compareTo("close", ignoreCase = true) == 0) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE)
            }
        }  else {
            // TODO: Make this a stream
            // TODO: This should encapsulate the above file stream also so we get ditch the virtual two points of return
            // TODO: The current file handling completely bypasses the postrequest interceptors ( badness9000 )
            var buffer = ""
            if (response.sendBuffer == null) {
                buffer = response.statusDescription
            } else if (response.sendBuffer is String) {
                if (response.sendBuffer as String != "") {
                    buffer = (response.sendBuffer as String)
                } else {
                    buffer = response.statusDescription
                }
            } else {
                if (response.negotiatedMediaType != "") {
                    val serializer = appServer.serializers.firstOrNull { it.canSerialize(response.negotiatedMediaType) }
                    if (serializer != null) {
                        response.contentType = response.negotiatedMediaType
                        buffer = serializer.serialize(response.sendBuffer!!)
                    } else {
                        response.setStatus(StatusCodes.UnsupportedMediaType)
                    }
                }
            }
            runInterceptors(postRequestInterceptors)

            httpResponse = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus(response.statusCode, response.statusDescription), Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8))
            response.setHeaders()
            addResponseHeaders(httpResponse, response)
            ctx.write(httpResponse)

            var lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)

            lastContentFuture.addListener(ChannelFutureListener.CLOSE)

        }
    }

    private fun addResponseHeaders(httpResponse: HttpResponse, response: Response) {
        for (header in response.rawHeaders) {
            if (header.value != "") {
                httpResponse.headers().add(header.key, header.value)
            }
        }
    }

    private fun deserializeBody(msg: HttpContent) {
        if (decoder != null) {
            decoder!!.offer(msg)
            request!!.bodyParams.putAll(deserializer!!.deserialize(decoder!!.getBodyHttpDatas()))
        } else {
            // TODO: Add support for CharSet
            val buffer = msg.content()
            if (buffer.isReadable()) {
                var data = buffer.toString(CharsetUtil.UTF_8)
                request!!.bodyParams.putAll(deserializer!!.deserialize(data!!))
            }
        }
    }
}