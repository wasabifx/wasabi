package org.wasabifx.wasabi.samples

import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.app.AppServer

fun main(args: Array<String>) {

    val server = AppServer(AppConfiguration(enableLogging = false))

    server.get("/", {
      /*  Runnable {
            Thread.sleep(3000)
      */
        response.send("Hello World!", contentType = "text/plain")
        //}
    })

    server.start()
}

