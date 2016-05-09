package org.wasabi.protocol.http2

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled.copiedBuffer
import io.netty.buffer.Unpooled.unreleasableBuffer
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpServerUpgradeHandler
import io.netty.handler.codec.http2.*
import io.netty.util.CharsetUtil
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.interceptors.InterceptorEntry
import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.InterceptOn
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabi.routing.Route
import org.wasabi.routing.RouteHandler
import java.util.*

class Http2Handler(appServer: AppServer, decoder: Http2ConnectionDecoder, encoder: Http2ConnectionEncoder, settings: Http2Settings) : Http2ConnectionHandler(decoder, encoder, settings), Http2FrameListener {

    private val logger = LoggerFactory.getLogger(Http2Handler::class.java)

    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val preExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreExecution }
    val postExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostExecution }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }
    var routeLocator = PatternAndVerbMatchingRouteLocator(appServer.routes)

    val RESPONSE_BYTES = unreleasableBuffer(copiedBuffer("Hello Wasabi", CharsetUtil.UTF_8));

    var currentSettings : Http2Settings? = null

    // TODO because within HTTP/2 we can have numerous requests and responses from the same client via multiple streams
    // TODO the interceptor chaining > handler execution needs to be done per stream in a stateless manner, need to relook
    // TODO at how this can be achieved, likely do such here and backport to the HTTP/1.* handler as it should clean
    // TODO things up nicely.

    val requests: Map<Int, Request> = HashMap()
    val responses: Map<Int, Response> = HashMap()

    private var bypassPipeline = false

    init {
        logger.info("http2 handler init")
    }

    private fun sendResponse(ctx: ChannelHandlerContext?, streamId: Int, payload: ByteBuf?) {
        // Send a frame for the response status
        val headers = DefaultHttp2Headers().status(StatusCodes.OK.code.toString());
        encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx!!.newPromise());
        encoder().writeData(ctx, streamId, payload, 0, true, ctx.newPromise());
        ctx.flush();
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
/*            val routeHandler = RouteHandler(request!!, response)

            routeHandler.handlerExtension()
            if (!routeHandler.executeNext) {
                break
            }*/
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
/*            val executeNext = interceptor.intercept(request!!, response)

            if (!executeNext) {
                bypassPipeline = true
                break
            }*/
        }
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        logger.info("userEventTriggered")
        if (evt is HttpServerUpgradeHandler.UpgradeEvent) {
            // If we get a non SSL HTTP/2 upgrade Write a response to the upgrade request
            val headers = DefaultHttp2Headers().status(StatusCodes.OK.code.toString());
            encoder().writeHeaders(ctx, 1, headers, 0, true, ctx!!.newPromise());
        }
        super.userEventTriggered(ctx, evt);
    }

    override fun onPingRead(ctx: ChannelHandlerContext?, data: ByteBuf?) {
        logger.info("onPingRead")
    }

    override fun onDataRead(ctx: ChannelHandlerContext?, streamId: Int, data: ByteBuf?, padding: Int, endOfStream: Boolean): Int {
        logger.info("onDataRead")
        val processed = data!!.readableBytes() + padding;
        if (endOfStream) {
            val headers = DefaultHttp2Headers().status(StatusCodes.OK.code.toString());
            encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx!!.newPromise());
            encoder().writeData(ctx, streamId, data, 0, true, ctx.newPromise());
            ctx.flush();
        }
        return processed;
    }

    override fun onSettingsRead(ctx: ChannelHandlerContext?, settings: Http2Settings?) {
        logger.info("onSettingsRead")
        this.currentSettings = settings
    }

    override fun onUnknownFrame(ctx: ChannelHandlerContext?, frameType: Byte, streamId: Int, flags: Http2Flags?, payload: ByteBuf?) {
        logger.info("onUnknownFrame")
    }

    override fun onHeadersRead(ctx: ChannelHandlerContext?, streamId: Int, headers: Http2Headers?, padding: Int, endOfStream: Boolean) {
        logger.info("onHeadersRead")
        logger.info(headers.toString())
    }

    override fun onHeadersRead(ctx: ChannelHandlerContext?, streamId: Int, headers: Http2Headers?, streamDependency: Int, weight: Short, exclusive: Boolean, padding: Int, endOfStream: Boolean) {
        logger.info("onHeadersRead")
        logger.info(headers.toString())
    }

    override fun onPushPromiseRead(ctx: ChannelHandlerContext?, streamId: Int, promisedStreamId: Int, headers: Http2Headers?, padding: Int) {
        logger.info("onPushPromiseRead")
    }

    override fun onPingAckRead(ctx: ChannelHandlerContext?, data: ByteBuf?) {
        logger.info("onPingAckRead")
    }

    override fun onRstStreamRead(ctx: ChannelHandlerContext?, streamId: Int, errorCode: Long) {
        logger.info("onRstStreamRead")
    }

    override fun onPriorityRead(ctx: ChannelHandlerContext?, streamId: Int, streamDependency: Int, weight: Short, exclusive: Boolean) {
        logger.info("onPriorityRead")
    }

    override fun onGoAwayRead(ctx: ChannelHandlerContext?, lastStreamId: Int, errorCode: Long, debugData: ByteBuf?) {
        logger.info("onGoAwayRead")
    }

    override fun onWindowUpdateRead(ctx: ChannelHandlerContext?, streamId: Int, windowSizeIncrement: Int) {
        logger.info("onWindowUpdateRead")

    }

    override fun onSettingsAckRead(ctx: ChannelHandlerContext?) {
        logger.info("HTTP2 Settings acknowledged")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        super.onError(ctx, cause)
        super.exceptionCaught(ctx, cause);
        logger.error("Exception Caught: $cause");
        cause!!.printStackTrace()
        ctx!!.close();
    }
}