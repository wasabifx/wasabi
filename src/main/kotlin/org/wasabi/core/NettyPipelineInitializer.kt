package org.wasabi.core

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.ssl.SslContext
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.ProtocolNegotiator


public class NettyPipelineInitializer(private val appServer: AppServer, private val sslContext: SslContext?):
                        ChannelInitializer<SocketChannel>() {

    private val logger = LoggerFactory.getLogger(NettyPipelineInitializer::class.java)

    override fun initChannel(channel: SocketChannel) {
        if (sslContext != null) initSslChannel(channel) else initBasicChannel(channel)
    }

    private fun initSslChannel(ch: SocketChannel) {
        ch.pipeline().addLast(sslContext!!.newHandler(ch.alloc()), ProtocolNegotiator(appServer));
    }

    private fun initBasicChannel(ch: SocketChannel) {
        logger.info("initBasicHandler")
        val pipeline = ch.pipeline()
        pipeline.addLast("decoder", HttpRequestDecoder())
        pipeline.addLast("encoder", HttpResponseEncoder())
        pipeline.addLast("aggregator", HttpObjectAggregator(1048576))
        pipeline.addLast("handler", HttpPipelineInitializer(appServer))
    }
}



