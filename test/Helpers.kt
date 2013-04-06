package org.wasabai.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.wasabi.http.HttpMethod
import org.wasabi.routing.Routes
import org.apache.http.client.methods.HttpDelete

object TestServer {

    public val appServer: AppServer = AppServer()

    public fun start() {
        appServer.start(false)
    }

    public fun stop() {
        appServer.stop()
    }

    public fun loadDefaultRoutes() {
        Routes.get("/", { response.send("Root")})
        Routes.get("/first", { response.send("First")})
    }
}

public fun delete(url: String): String {
    val httpClient = DefaultHttpClient()


    val responseHandler = BasicResponseHandler()

    val httpDelete = HttpDelete(url)
    httpDelete.setHeader("User-Agent", "test-client")
    httpDelete.setHeader("Connection", "keep-alive")
    httpDelete.setHeader("Cache-Control", "max-age=0")
    httpDelete.setHeader("Accept", "Accept=text/html,application/xhtml+xml,application/xml")
    httpDelete.setHeader("Accept-Encoding", "gzip,deflate,sdch")
    httpDelete.setHeader("Accept-Language", "en-US,en;q=0.8")
    httpDelete.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3")


    return httpClient.execute(httpDelete, responseHandler)!!

}

public fun get(url: String): String {

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




