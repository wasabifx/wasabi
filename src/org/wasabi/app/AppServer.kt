package org.wasabi.app

import org.wasabi.routing.Routes
import org.wasabi.http.HttpServer
import org.wasabi.configuration.ConfigurationStorage


public class AppServer(configurationFilename: String = "", val routes: Routes = Routes()) {

    val configuration: AppConfiguration

    private val httpServer: HttpServer
    {

        if (configurationFilename != "") {

            val configurationStorage = ConfigurationStorage()
            configuration = configurationStorage.loadFromFile(configurationFilename)
        } else {
            configuration = AppConfiguration()
        }

        httpServer = HttpServer(configuration, routes)
    }

    public fun start() {
        httpServer.start()

    }

    public fun stop() {
        httpServer.stop()
    }


}

