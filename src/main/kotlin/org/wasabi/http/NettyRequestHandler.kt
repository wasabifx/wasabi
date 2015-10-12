package org.wasabi.http

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import io.netty.handler.stream.ChunkedNioFile
import io.netty.util.CharsetUtil
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.deserializers.Deserializer
import org.wasabi.interceptors.InterceptorEntry
import org.wasabi.routing.*
import org.wasabi.websocket.WebSocketHandler
import java.io.FileInputStream


// TODO: This class needs cleaning up
public class NettyRequestHandler(private val appServer: AppServer, routeLocator: RouteLocator, channelLocator: ChannelLocator): SimpleChannelInboundHandler<Any?>(), RouteLocator by routeLocator , ChannelLocator by channelLocator{

    var request: Request? = null
    var body = ""
    val response = Response()
    var chunkedTransfer = false
    var decoder : HttpPostRequestDecoder? = null
    val factory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val preExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreExecution }
    val postExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostExecution }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }
    var deserializer : Deserializer? = null

    private var handshaker : WebSocketServerHandshaker? = null;

    private var log = LoggerFactory.getLogger(NettyRequestHandler::class.java)

    private var bypassPipeline = false

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {

        if (msg is WebSocketFrame)
        {
            // TODO make sure channel locator can be injected also
            WebSocketHandler(appServer, PatternMatchingChannelLocator(appServer.channels)).handleWebSocketRequest(handshaker!!, ctx!!, msg)
        }


        if (msg is FullHttpRequest)
        {
            // Here we catch the upgrade request and setup handshaker factory to negotiate client connection
            if ( msg is HttpRequest && msg.headers().get(HttpHeaders.Names.UPGRADE) == "websocket")
            {
                // Setup Handshake
                var wsFactory : WebSocketServerHandshakerFactory = WebSocketServerHandshakerFactory(msg.getUri(), null, false);

                handshaker = wsFactory.newHandshaker(msg)

                try {

                    log!!.info(handshaker?.uri())

                    findChannelHandler(handshaker?.uri().toString()).handler

                    if (handshaker == null) {
                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx?.channel()!!);
                    } else {
                        handshaker?.handshake(ctx?.channel()!!, msg);
                    }
                    return
                }
                catch(exception: RouteNotFoundException)
                {
                    // If we dont have a channel to support the requested URL we set not found and bail out.
                    response.setStatus(StatusCodes.NotFound)
                    writeResponse(ctx!!, response)
                    return
                }
            }
            handleStandardHttpRequest(ctx, msg)
        }
    }




    private fun handleStandardHttpRequest(ctx: ChannelHandlerContext?, msg: Any?)
    {
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

                    val routeHandlers = findRouteHandlers(request!!.uri.split('?')[0], request!!.method)
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
                    log!!.debug("Exception during web invocation: ${e.getMessage()}")
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
            interceptorsToRun = interceptors.filter { compareRouteSegments(route, it.path) }
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

            httpResponse = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus(response.statusCode,response.statusDescription),  Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8))
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

    public override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        log!!.debug("Exception during web invocation: ${cause?.getMessage()}")
        log!!.debug(cause?.getStackTrace().toString())
        response.setStatus(StatusCodes.InternalServerError)
        writeResponse(ctx, response)
    }


    private fun deserializeBody(msg: HttpContent) {
        // TODO: Re-structure all this and fix it as it requires a special case for decoder
        // TODO: Check we actually need to bother calling deserializer etc.
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

