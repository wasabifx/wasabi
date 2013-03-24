package com.hadihariri.wasabi.http

import org.jboss.netty.channel.MessageEvent

public class Response(private val nexus: MessageEvent) {

    fun write(data: Any) {
        nexus.getChannel()?.write(data)
    }

    fun send(message: String) {
        write(message)
    }
}
