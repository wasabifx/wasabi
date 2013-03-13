package com.hadihariri.wasabi

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

    public fun start() {
        httpServer.start()

    }

    public fun stop() {
        httpServer.stop()
    }

}