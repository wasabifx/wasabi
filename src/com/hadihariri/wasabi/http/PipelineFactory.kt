package com.hadihariri.wasabi.http

import org.jboss.netty.channel.ChannelPipelineFactory
import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.channel.Channels
import com.hadihariri.wasabi.routing.Routes

public class PipelineFactory(private val routes: Routes): ChannelPipelineFactory {
    public override fun getPipeline(): ChannelPipeline? {
        val pipeline = Channels.pipeline()!!
     //   pipeline.addFirst("connect", ConnectHandler())
        pipeline.addFirst("handler", RequestHandler(routes))

        return pipeline
    }

}