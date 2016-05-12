package org.wasabi.core

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpServerUpgradeHandler
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory
import io.netty.handler.codec.http2.Http2CodecUtil
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec
import io.netty.handler.ssl.SslContext
import io.netty.util.AsciiString
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.ProtocolNegotiator
import org.wasabi.protocol.http2.Http2HandlerBuilder
import org.wasabi.protocol.websocket.WebSocketUpgradeCodec


public class NettyPipelineInitializer(private val appServer: AppServer, private val sslContext: SslContext?):
                        ChannelInitializer<SocketChannel>() {

    private val logger = LoggerFactory.getLogger(NettyPipelineInitializer::class.java)

    private val upgradeFactory = UpgradeCodecFactory { protocol ->
        logger.info("Into newUpgradeCodec")
        if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol))
        {
            return@UpgradeCodecFactory Http2ServerUpgradeCodec(Http2HandlerBuilder(appServer).build())
        }
        if (AsciiString.contentEquals("websocket", protocol))
        {
            logger.info("Websocket upgrade")
            return@UpgradeCodecFactory WebSocketUpgradeCodec(appServer, "websocket")
        }

        logger.info("About to throw exception...")
        throw Exception("We should correctly handle unknown protocol on upgrade.")
    }

    override fun initChannel(channel: SocketChannel) {
        if (sslContext != null) initSslChannel(channel) else initBasicChannel(channel)
    }

    private fun initSslChannel(ch: SocketChannel) {
        ch.pipeline().addLast(sslContext!!.newHandler(ch.alloc()), ProtocolNegotiator(appServer));
    }

    private fun initBasicChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()
        val codec = HttpServerCodec();
        pipeline.addLast(codec);
        pipeline.addLast("upgrade", HttpServerUpgradeHandler(codec, upgradeFactory))
        pipeline.addLast("handler", HttpPipelineInitializer(appServer))
    }
}



