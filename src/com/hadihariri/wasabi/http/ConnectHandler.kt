package com.hadihariri.wasabi.http

import org.jboss.netty.channel.SimpleChannelHandler
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.ChannelStateEvent
import org.jboss.netty.channel.ExceptionEvent
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.buffer.ChannelBuffers

public class ConnectHandler(): SimpleChannelUpstreamHandler() {
    public override fun exceptionCaught(ctx: ChannelHandlerContext?, e: ExceptionEvent?) {
        e?.getChannel()?.close()
    }

}