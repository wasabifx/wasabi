package org.wasabai.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.wasabi.http.HttpMethod

object TestServer {

    public val appServer: AppServer = AppServer()

    public fun start() {
        appServer.start(false)
    }

    public fun stop() {
        appServer.stop()
    }

    public fun loadDefaultRoutes() {
        appServer.routes.addRoute(HttpMethod.GET, "/", { req, res -> res.send("Root")})
        appServer.routes.addRoute(HttpMethod.GET, "/first", { req, res -> res.send("First")})
    }
}

public fun Get(url: String): String {

    val httpClient = DefaultHttpClient()


    val responseHandler = BasicResponseHandler()

    val httpGet = HttpGet(url)
    httpGet.setHeader("User-Agent", "test-client")
    httpGet.setHeader("Connection", "keep-alive")
    httpGet.setHeader("Cache-Control", "max-age=0")
    httpGet.setHeader("Accept", "Accept=text/html,application/xhtml+xml,application/xml")


    return httpClient.execute(httpGet, responseHandler)!!

}



