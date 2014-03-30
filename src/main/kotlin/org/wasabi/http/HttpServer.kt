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
    val primaryGroup : NioEventLoopGroup
    val workerGroup :  NioEventLoopGroup

    {
        // Define worker groups
        primaryGroup = NioEventLoopGroup()
        workerGroup = NioEventLoopGroup()

        // Initialize bootstrap of server
        bootstrap = ServerBootstrap()

        bootstrap.group(primaryGroup, workerGroup)
        bootstrap.channel(javaClass<NioServerSocketChannel>())
        bootstrap.childHandler(NettyPipelineInitializer(appServer))

    }


    public fun start(wait: Boolean = true) {
        val channel = bootstrap.bind(appServer.configuration.port)?.sync()?.channel()

        if (wait) {
            channel?.closeFuture()?.sync()
        }
    }

    public fun stop() {

        // Shutdown all event loops
        primaryGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()

        // Wait till all threads are terminated
        primaryGroup.terminationFuture().sync()
        workerGroup.terminationFuture().sync()

    }


}