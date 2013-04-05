package org.wasabi.app

import org.wasabi.routing.Routes
import org.wasabi.http.HttpServer
import org.wasabi.configuration.ConfigurationStorage


public class AppServer(configurationFilename: String = "") {

    val configuration: AppConfiguration

    private val httpServer: HttpServer
    {

        if (configurationFilename != "") {

            val configurationStorage = ConfigurationStorage()
            configuration = configurationStorage.loadFromFile(configurationFilename)
        } else {
            configuration = AppConfiguration()
        }

        httpServer = HttpServer(configuration)
    }

    public fun start(wait: Boolean = true) {
        httpServer.start(wait)

    }

    public fun stop() {
        httpServer.stop()
    }


}

