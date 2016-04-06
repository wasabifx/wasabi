package org.wasabi.protocol.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.ssl.ApplicationProtocolNames
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler
import org.wasabi.app.AppServer

/**
 * Created by swishy on 5/04/16.
 */
class MultiProtocolHandler(val appserver: AppServer) : ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_1_1) {
    override fun configurePipeline(ctx: ChannelHandlerContext?, protocol: String?) {
        when(protocol) {
            ApplicationProtocolNames.HTTP_1_1 -> {
                ctx!!.pipeline().addLast(HttpServerCodec(),
                        HttpObjectAggregator(1048576),
                        NettyRequestHandler(appserver));
                return;
            }
            ApplicationProtocolNames.HTTP_2 -> {
                throw NotImplementedError("Coming soon.")
            }
        }
    }
}