package org.wasabai.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.wasabi.http.HttpMethod
import org.wasabi.routing.Routes
import org.apache.http.client.methods.HttpDelete
import org.apache.http.Header
import java.util.HashMap
import java.util.Dictionary

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

public fun delete(url: String, headers: HashMap<String, String>): HttpClientResponse {
    val httpClient = DefaultHttpClient()


    val responseHandler = BasicResponseHandler()

    val httpDelete = HttpDelete(url)
    for ((key, value) in headers) {
        httpDelete.setHeader(key, value)
    }

    return HttpClientResponse(httpDelete.getAllHeaders()!!, httpClient.execute(httpDelete, responseHandler)!!)

}

public fun get(url: String, headers: HashMap<String,String>): HttpClientResponse {

    val httpClient = DefaultHttpClient()


    val responseHandler = BasicResponseHandler()

    val httpGet = HttpGet(url)
    for ((key, value) in headers) {
        httpGet.setHeader(key, value)
    }


    return HttpClientResponse(httpGet.getAllHeaders()!!, httpClient.execute(httpGet, responseHandler)!!)

}

data public class HttpClientResponse(val headers: Array<Header>, val body: String)



