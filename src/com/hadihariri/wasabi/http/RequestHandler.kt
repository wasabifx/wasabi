package com.hadihariri.wasabi.http

import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.MessageEvent
import com.hadihariri.wasabi.routing.Routes
import org.jboss.netty.channel.ExceptionEvent

public class RequestHandler(private val routes: Routes): SimpleChannelUpstreamHandler() {
    public override fun messageReceived(ctx: ChannelHandlerContext?, e: MessageEvent?) {
        // examine the request
        // lookup the handler
        // execute
        val handler = routes.findHandler(HttpMethod.GET, "/")

        handler.invoke(Request(), Response(e!!))
    }
    public override fun exceptionCaught(ctx: ChannelHandlerContext?, e: ExceptionEvent?) {
//        super<SimpleChannelUpstreamHandler>.exceptionCaught(ctx, e)
    }


}