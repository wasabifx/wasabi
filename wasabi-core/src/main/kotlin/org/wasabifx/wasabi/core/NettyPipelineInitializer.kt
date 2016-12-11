package org.wasabifx.wasabi.core

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.ssl.SslContext
import io.netty.handler.stream.ChunkedWriteHandler
import org.slf4j.LoggerFactory
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.app.configuration
import org.wasabifx.wasabi.protocol.http.HttpRequestHandler

var inc = 0

class NettyPipelineInitializer(private val appServer: AppServer, private val sslContext: SslContext?):
                        ChannelInitializer<SocketChannel>() {
    override fun initChannel(channel: SocketChannel) {
        if (sslContext != null) initSslChannel(channel) else initBasicChannel(channel)
    }

    private fun initSslChannel(ch: SocketChannel) {
        //ch.pipeline().addLast(sslContext!!.newHandler(ch.alloc()), ProtocolNegotiator(appServer))
    }

    private fun initBasicChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()
        pipeline.addLast("codec", HttpServerCodec())
        pipeline.addAfter(pipeline.context(this).name(), "chunkedWriter", ChunkedWriteHandler())
        pipeline.addLast("aggregator", HttpObjectAggregator(configuration!!.maxHttpContentLength))
        pipeline.addLast(HttpRequestHandler(appServer))
        //pipeline.addLast("handler", HttpPipelineInitializer(appServer))
    }
}



