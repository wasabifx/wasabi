package org.wasabi.core

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http2.Http2CodecUtil
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.util.AsciiString
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.HttpInitializer
import org.wasabi.protocol.http2.Http2HandlerBuilder


public class NettyPipelineInitializer(private val appServer: AppServer):
                        ChannelInitializer<SocketChannel>() {

    private val logger = LoggerFactory.getLogger(NettyPipelineInitializer::class.java)

    private val upgradeFactory = object: UpgradeCodecFactory {
        override fun newUpgradeCodec(protocol:CharSequence): HttpServerUpgradeHandler.UpgradeCodec {
            logger.debug("Into newUpgradeCodec")
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol))
            {
                return Http2ServerUpgradeCodec(Http2HandlerBuilder().build())
            }

            logger.debug("About to throw exception...")
            throw Exception("We should correctly handle unknown protocol on upgrade.")
        }
    }

    protected override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()
        val codec = HttpServerCodec();
        pipeline.addLast(codec);
        pipeline.addLast("upgrade", HttpServerUpgradeHandler(codec, upgradeFactory))
        pipeline.addLast("handler", HttpInitializer(appServer))
    }
}



