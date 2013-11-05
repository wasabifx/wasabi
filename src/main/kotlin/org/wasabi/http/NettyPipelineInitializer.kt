package org.wasabi.http

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.HttpObjectAggregator
import java.nio.channels.Channels
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabi.app.AppServer
import io.netty.handler.stream.ChunkedWriteHandler
import org.wasabi.routing.PatternMatchingChannelLocator


public class NettyPipelineInitializer(private val appServer: AppServer):
                        ChannelInitializer<SocketChannel>() {
    protected override fun initChannel(ch: SocketChannel?) {
        val pipeline = ch?.pipeline()
        pipeline?.addLast("decoder", HttpRequestDecoder())
        pipeline?.addLast("encoder", HttpResponseEncoder())
        pipeline?.addLast("chunkedWriter", ChunkedWriteHandler());
        pipeline?.addLast("aggregator", HttpObjectAggregator(1048576));
        pipeline?.addLast("handler", NettyRequestHandler(appServer, PatternAndVerbMatchingRouteLocator(appServer.routes), PatternMatchingChannelLocator(appServer.channels)))
    }

}



