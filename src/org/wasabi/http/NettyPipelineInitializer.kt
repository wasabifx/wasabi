package org.wasabi.http

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import java.nio.channels.Channels
import org.wasabi.routing.Routes
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabi.app.AppServer


public class NettyPipelineInitializer(private val appServer: AppServer): ChannelInitializer<SocketChannel>() {
    protected override fun initChannel(ch: SocketChannel?) {
        val pipeline = ch?.pipeline()
        pipeline?.addLast("decoder", HttpRequestDecoder())
        pipeline?.addLast("encoder", HttpResponseEncoder())
        pipeline?.addLast("handler", NettyRequestHandler(appServer, PatternAndVerbMatchingRouteLocator()))
    }

}