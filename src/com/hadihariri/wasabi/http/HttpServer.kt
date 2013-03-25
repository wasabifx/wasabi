package com.hadihariri.wasabi.http

import java.util.concurrent.Executors
import java.net.InetSocketAddress
import com.hadihariri.wasabi.app.AppConfiguration
import com.hadihariri.wasabi.routing.Routes
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel


public class HttpServer(private val configuration: AppConfiguration, private val routes: Routes) {

    val bootstrap: ServerBootstrap

    {
        bootstrap = ServerBootstrap()

        bootstrap.group(NioEventLoopGroup(), NioEventLoopGroup())
        bootstrap.channel(javaClass<NioServerSocketChannel>())
        bootstrap.childHandler(ServerInitializer(routes))

    }


    public fun start() {
        val channel = bootstrap.bind(configuration.port)?.sync()?.channel()
        channel?.closeFuture()?.sync()
    }

    public fun stop() {
        bootstrap.shutdown()
    }


}