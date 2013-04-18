package org.wasabi.app

import org.wasabi.routing.Routes
import org.wasabi.http.HttpServer
import org.wasabi.configuration.ConfigurationStorage


public class AppServer(val configuration: AppConfiguration = AppConfiguration()) {

    private val httpServer: HttpServer
    private var running = false

    {
        httpServer = HttpServer(configuration)
    }

    public val isRunning : Boolean
        get ()
            {return running}

    public fun start(wait: Boolean = true) {
        httpServer.start(wait)
        running = true

    }

    public fun stop() {
        httpServer.stop()
        running = false
    }




}

