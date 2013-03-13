package com.hadihariri.wasabi

import org.jboss.netty.channel.ChannelPipelineFactory
import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.channel.Channels

public class PipelineFactory: ChannelPipelineFactory {
    public override fun getPipeline(): ChannelPipeline? {
        return Channels.pipeline(ConnectHandler())
    }

}