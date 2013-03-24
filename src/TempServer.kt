package com.hadihariri.wasabi

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.channel.ChannelPipelineFactory
import java.util.concurrent.Executors
import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.channel.Channels
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.MessageEvent
import org.jboss.netty.channel.ExceptionEvent
import java.util.logging.Logger
import java.util.concurrent.atomic.AtomicLong
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.util.logging.Level
import java.net.InetSocketAddress
import javax.sql.rowset.spi.XmlWriter


public class EchoServerHandler: SimpleChannelUpstreamHandler() {

    val logger: Logger = Logger.getLogger("EchoServerHandler")
    val transferredBytes = AtomicLong()

    public override fun messageReceived(ctx: ChannelHandlerContext?, e: MessageEvent?) {
        transferredBytes.addAndGet((e?.getMessage() as ChannelBuffer).readableBytes())
        e?.getChannel()?.write(e?.getMessage())
    }

    public override fun exceptionCaught(ctx: ChannelHandlerContext?, e: ExceptionEvent?) {
        logger.log(Level.WARNING, "Unexpected exception", e?.getCause())
        e?.getChannel()?.close()
    }

}



