package org.wasabi.http

import java.util.concurrent.Executors
import java.net.InetSocketAddress
import org.wasabi.app.AppConfiguration
import org.wasabi.routing.Routes
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel


public class HttpServer(private val configuration: AppConfiguration) {

    val bootstrap: ServerBootstrap

    {
        bootstrap = ServerBootstrap()

        bootstrap.group(NioEventLoopGroup(), NioEventLoopGroup())
        bootstrap.channel(javaClass<NioServerSocketChannel>())
        bootstrap.childHandler(ServerInitializer(Routes))

    }


    public fun start(wait: Boolean = true) {
        val channel = bootstrap.bind(configuration.port)?.sync()?.channel()

        if (wait) {
            channel?.closeFuture()?.sync();
        }
    }

    public fun stop() {
        bootstrap.shutdown()
    }


}