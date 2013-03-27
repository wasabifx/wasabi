package org.wasabai.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet

object TestServer {

    public val appServer: AppServer = AppServer()

    public fun start() {
        appServer.start(false)
    }

    public fun stop() {
        appServer.stop()
    }
}

public fun Get(url: String): String {

    val httpClient = DefaultHttpClient()

    val responseHandler = BasicResponseHandler()

    val httpGet = HttpGet(url)

    return httpClient.execute(httpGet, responseHandler)!!

}

