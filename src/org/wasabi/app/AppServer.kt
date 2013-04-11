package org.wasabi.app

import org.wasabi.routing.Routes
import org.wasabi.http.HttpServer
import org.wasabi.configuration.ConfigurationStorage


public class AppServer(val configuration: AppConfiguration = AppConfiguration()) {

    private val httpServer: HttpServer

    {
        httpServer = HttpServer(configuration)
    }

    public fun start(wait: Boolean = true) {
        httpServer.start(wait)

    }

    public fun stop() {
        httpServer.stop()
    }


}

