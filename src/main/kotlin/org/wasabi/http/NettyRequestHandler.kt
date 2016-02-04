package org.wasabi.http

import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.LastHttpContent
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.routing.PatternMatchingChannelLocator
import org.wasabi.routing.RouteNotFoundException
import org.wasabi.websocket.WebSocketHandler


// TODO: This class needs cleaning up
public class NettyRequestHandler(private val appServer: AppServer): SimpleChannelInboundHandler<Any?>() {

    var request: Request? = null
    val response = Response()

    private var handshaker : WebSocketServerHandshaker? = null;

    private var log = LoggerFactory.getLogger(NettyRequestHandler::class.java)

    // TODO See comment down further....
    private var channelLocator = PatternMatchingChannelLocator(appServer.channels)

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {

        if (msg is WebSocketFrame)
        {
            WebSocketHandler(appServer, channelLocator).handleRequest(handshaker!!, ctx!!, msg)
        }


        if (msg is FullHttpRequest)
        {
            // Here we catch the upgrade request and setup handshaker factory to negotiate client connection
            if ( msg is HttpRequest && msg.headers().get(HttpHeaders.Names.UPGRADE) == "websocket")
            {
                // Setup Handshake
                var wsFactory : WebSocketServerHandshakerFactory = WebSocketServerHandshakerFactory(msg.getUri(), null, false);

                handshaker = wsFactory.newHandshaker(msg)
                
                // TODO move into new websocket handler? do we want to complete handshake if theres no channelHandler?
                try {

                    log!!.info(handshaker?.uri())

                    channelLocator.findChannelHandler(handshaker?.uri().toString()).handler

                    if (handshaker == null) {
                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx?.channel()!!);
                    } else {
                        handshaker?.handshake(ctx?.channel()!!, msg);
                    }
                    return
                }
                catch(exception: RouteNotFoundException)
                {
                    // If we dont have a handler to support the requested URL we set not found and bail out.
                    response.setStatus(StatusCodes.NotFound)
                    response.setHeaders()
                    ctx!!.write(response)
                    var lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
                    lastContentFuture.addListener(ChannelFutureListener.CLOSE)
                    return
                }
            }
            HttpRequestHandler(appServer).handleRequest(ctx, msg)
        }
    }

    public override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        log!!.debug("Exception during web invocation: ${cause?.message}")
        log!!.debug(cause?.stackTrace.toString())
        response.setStatus(StatusCodes.InternalServerError)
        response.setHeaders()
        ctx.write(response)
        var lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
        lastContentFuture.addListener(ChannelFutureListener.CLOSE)
    }
}

