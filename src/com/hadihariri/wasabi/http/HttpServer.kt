package com.hadihariri.wasabi.http

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
import com.hadihariri.wasabi.app.AppConfiguration
import com.hadihariri.wasabi.http.PipelineFactory
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.MessageEvent
import com.hadihariri.wasabi.routing.Routes


public class HttpServer(private val configuration: AppConfiguration, private val routes: Routes) {

    val bootstrap: ServerBootstrap

    {
        bootstrap = ServerBootstrap(
            NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool()))
        bootstrap.setPipelineFactory(PipelineFactory(routes))
    }


    public fun start() {
        bootstrap.bind(InetSocketAddress(configuration.port))
    }

    public fun stop() {
        bootstrap.shutdown()
    }


}