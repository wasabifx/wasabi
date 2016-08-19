package org.wasabifx.protocol.http2

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.buffer.Unpooled.copiedBuffer
import io.netty.buffer.Unpooled.unreleasableBuffer
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpServerUpgradeHandler
import io.netty.handler.codec.http2.*
import io.netty.util.CharsetUtil
import org.slf4j.LoggerFactory
import org.wasabifx.app.AppServer
import org.wasabifx.interceptors.InterceptorEntry
import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.wasabifx.protocol.http.StatusCodes
import org.wasabifx.routing.InterceptOn
import org.wasabifx.routing.PatternAndVerbMatchingRouteLocator
import org.wasabifx.routing.Route
import org.wasabifx.routing.RouteHandler
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.*

class Http2Handler(val appServer: AppServer, decoder: Http2ConnectionDecoder, encoder: Http2ConnectionEncoder, settings: Http2Settings) : Http2ConnectionHandler(decoder, encoder, settings), Http2FrameListener {

    private val log = LoggerFactory.getLogger(Http2Handler::class.java)

    // TODO Assess the best place to implement the prerequest interceptor execution.
    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val preExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreExecution }
    val postExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostExecution }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }
    var routeLocator = PatternAndVerbMatchingRouteLocator(appServer.routes)

    var currentSettings : Http2Settings? = null

    val requests: MutableMap<Int, Request> = hashMapOf()
    val responses: MutableMap<Int, Response> = hashMapOf()

    // TODO implement proper use of windowsize and settings

    private var bypassPipeline = false

    private fun executePipeline(streamId: Int, request: Request, ctx: ChannelHandlerContext) {
        val routeHandlers = routeLocator.findRouteHandlers(request.path, HttpMethod(request.method.name()))

        // process the route specific pre execution interceptors
        runInterceptors(streamId, preExecutionInterceptors, routeHandlers)

        // Execute the handlers for this route.
        runHandlers(streamId, routeHandlers)

        // process the route specific post execution interceptors
        runInterceptors(streamId, postExecutionInterceptors, routeHandlers)

        // Run global interceptors again
        runInterceptors(streamId, postExecutionInterceptors)

        writeResponse(ctx, streamId, responses[streamId]!!)
    }

    private fun writeResponse(ctx: ChannelHandlerContext?, streamId: Int, response: Response) {
        // If we have a non successful status make sure the error interceptors know about it.
        if (response.statusCode / 100 == 4 || response.statusCode / 100 == 5) {
            runInterceptors(streamId, errorInterceptors)
        }

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

        runInterceptors(streamId, postRequestInterceptors)

        val headers = DefaultHttp2Headers().status(response.statusCode.toString());
        encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx!!.newPromise());
        encoder().writeData(ctx, streamId, Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8), 0, true, ctx.newPromise());
        ctx.flush();
    }

    private fun runHandlers(streamId: Int, routeHandlers : Route)
    {
        val request = this.requests[streamId]
        val response = Response()

        // Assign to collection for later use.
        this.responses.put(streamId, response)

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

    private fun runInterceptors(streamId: Int, interceptors: List<InterceptorEntry>, route: Route? = null) {
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
        for ((interceptor) in interceptorsToRun) {

            val executeNext = interceptor.intercept(requests[streamId]!!, responses[streamId]!!)

            if (!executeNext) {
                bypassPipeline = true
                break
            }
        }
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        if (evt is HttpServerUpgradeHandler.UpgradeEvent) {
            log.debug("HTTP/2 upgrade requested")
            // If we get a non SSL HTTP/2 upgrade Write a response to the upgrade request
            val headers = DefaultHttp2Headers().status(StatusCodes.OK.code.toString());
            encoder().writeHeaders(ctx, 1, headers, 0, true, ctx!!.newPromise());
        }
        super.userEventTriggered(ctx, evt);
    }

    override fun onPingRead(ctx: ChannelHandlerContext?, data: ByteBuf?) {
        // Handled by Netty
    }

    override fun onDataRead(ctx: ChannelHandlerContext?, streamId: Int, data: ByteBuf?, padding: Int, endOfStream: Boolean): Int {
        val processed = data!!.readableBytes() + padding;
        try{
            val request = requests[streamId]
            if (endOfStream) {
                executePipeline(streamId, request!!, ctx!!)
            }
        }
        catch(exception: Exception)
        {
            log.error(exception.message)
        }

        return processed;
    }

    override fun onSettingsRead(ctx: ChannelHandlerContext?, settings: Http2Settings?) {
        this.currentSettings = settings
    }

    override fun onUnknownFrame(ctx: ChannelHandlerContext?, frameType: Byte, streamId: Int, flags: Http2Flags?, payload: ByteBuf?) {
        log.debug("onUnknownFrame")
    }

    override fun onHeadersRead(ctx: ChannelHandlerContext?, streamId: Int, headers: Http2Headers?, padding: Int, endOfStream: Boolean) {
        // TODO work out use case of the simplified overload.
        log.debug("onHeadersRead")
        log.debug(headers.toString())
    }

    override fun onHeadersRead(ctx: ChannelHandlerContext?, streamId: Int, headers: Http2Headers?, streamDependency: Int, weight: Short, exclusive: Boolean, padding: Int, endOfStream: Boolean) {
        log.debug(headers.toString())
        val request = Request(headers, ctx!!.channel().remoteAddress() as InetSocketAddress)
        requests[streamId] = request
        if (endOfStream || request.method.toString() == "GET") {
            try {
                executePipeline(streamId, request, ctx)
            }
            catch(exception : Exception) {
                log.error(exception.message)
            }
        }
    }

    override fun onPushPromiseRead(ctx: ChannelHandlerContext?, streamId: Int, promisedStreamId: Int, headers: Http2Headers?, padding: Int) {
        log.debug("onPushPromiseRead")
    }

    override fun onPingAckRead(ctx: ChannelHandlerContext?, data: ByteBuf?) {
        log.debug("onPingAckRead")
    }

    override fun onRstStreamRead(ctx: ChannelHandlerContext?, streamId: Int, errorCode: Long) {
        log.debug("onRstStreamRead")
    }

    override fun onPriorityRead(ctx: ChannelHandlerContext?, streamId: Int, streamDependency: Int, weight: Short, exclusive: Boolean) {
        log.debug("onPriorityRead")
    }

    override fun onGoAwayRead(ctx: ChannelHandlerContext?, lastStreamId: Int, errorCode: Long, debugData: ByteBuf?) {
        log.debug("onGoAwayRead")
    }

    override fun onWindowUpdateRead(ctx: ChannelHandlerContext?, streamId: Int, windowSizeIncrement: Int) {
        log.debug("onWindowUpdateRead")
    }

    override fun onSettingsAckRead(ctx: ChannelHandlerContext?) {
        log.debug("HTTP2 Settings acknowledged")

    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        super.onError(ctx, cause)
        super.exceptionCaught(ctx, cause);
        log.error("Exception Caught: $cause");
        cause!!.printStackTrace()
        ctx!!.close();
    }
}