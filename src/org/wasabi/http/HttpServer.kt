package org.wasabi.http

import java.util.concurrent.Executors
import java.net.InetSocketAddress
import org.wasabi.app.AppConfiguration
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.wasabi.app.AppServer


public class HttpServer(private val appServer: AppServer) {

    val bootstrap: ServerBootstrap

    {
        bootstrap = ServerBootstrap()

        bootstrap.group(NioEventLoopGroup(), NioEventLoopGroup())
        bootstrap.channel(javaClass<NioServerSocketChannel>())
        bootstrap.childHandler(NettyPipelineInitializer(appServer))

    }


    public fun start(wait: Boolean = true) {
        val channel = bootstrap.bind(appServer.configuration.port)?.sync()?.channel()

        if (wait) {
            channel?.closeFuture()?.sync();
        }
    }

    public fun stop() {
        bootstrap.shutdown()
    }


}