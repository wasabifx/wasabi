package com.hadihariri.wasabi.http


public class Response(private val nexus: Any) {

    fun write(data: Any) {
       // nexus.getChannel()?.write(data)
    }

    fun send(message: String) {
        write(message)
    }
}
