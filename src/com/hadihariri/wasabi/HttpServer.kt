package com.hadihariri.wasabi

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress


public class HttpServer(private val configuration: AppConfiguration) {

    val bootstrap: ServerBootstrap

    {
        bootstrap = ServerBootstrap(
            NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool()))
        bootstrap.setPipelineFactory(PipelineFactory())
    }


    public fun start() {
        bootstrap.bind(InetSocketAddress(configuration.port))
    }

    public fun stop() {
        bootstrap.shutdown()
    }
}