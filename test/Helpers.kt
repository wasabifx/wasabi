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
        appServer.routes.get("/", { req, res -> res.send("Root")})
        appServer.routes.get("/first", { req, res -> res.send("First")})
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
    httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch")
    httpGet.setHeader("Accept-Language", "en-US,en;q=0.8")
    httpGet.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3")


    return httpClient.execute(httpGet, responseHandler)!!

}



