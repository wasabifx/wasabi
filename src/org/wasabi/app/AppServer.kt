package org.wasabi.app

import org.wasabi.routing.Routes
import org.wasabi.http.HttpServer
import org.wasabi.configuration.ConfigurationStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public class AppServer(val configuration: AppConfiguration = AppConfiguration()) {

    private var logger = LoggerFactory.getLogger(javaClass<AppServer>())
    private val httpServer: HttpServer
    private var running = false

    {
        httpServer = HttpServer(configuration)
    }

    public val isRunning : Boolean
        get ()
            {return running}

    public fun start(wait: Boolean = true) {
        logger!!.info(configuration.welcomeMessage)
        running = true
        httpServer.start(wait)

    }

    public fun stop() {
        httpServer.stop()
        logger!!.info("Server Stopped")
        running = false
    }




}

