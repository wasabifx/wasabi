package org.wasabi.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import org.wasabi.app.AppServer
import org.wasabi.routing.ChannelLocator
import org.wasabi.routing.InterceptOn

public class WebSocketHandler(private val appServer: AppServer, channelLocator: ChannelLocator) : ChannelLocator by channelLocator {

    val preRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreRequest }
    val preExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PreExecution }
    val postExecutionInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostExecution }
    val postRequestInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.PostRequest }
    val errorInterceptors = appServer.interceptors.filter { it.interceptOn == InterceptOn.Error }

    fun handleWebSocketRequest(handshaker: WebSocketServerHandshaker, ctx: ChannelHandlerContext, webSocketFrame: WebSocketFrame)
    {
        // Check for closing websocket frame
        // TODO this should probably be allowed to be handled in handler for cleanup etc.
        if (webSocketFrame is CloseWebSocketFrame)
        {
            handshaker.close(ctx.channel(), webSocketFrame.retain())
        }

        // Grab the handler for the current channel.
        val handler = findChannelHandler(handshaker.uri().toString()).handler
        val channelExtension : ChannelHandler.() -> Unit = handler
        val channelHandler = ChannelHandler(ctx, webSocketFrame)
        channelHandler.channelExtension()

        // TODO look to hook write here.... for post interceptors.
    }
}