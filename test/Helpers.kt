package org.wasabai.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.wasabi.routing.Routes
import org.apache.http.client.methods.HttpDelete
import org.apache.http.Header
import java.util.HashMap
import java.util.Dictionary
import org.apache.http.client.methods.HttpPost
import org.apache.http.HttpEntity
import org.apache.http.client.entity.UrlEncodedFormEntity
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.methods.HttpRequestBase

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

    public val isRunning : Boolean = appServer.isRunning
}

private fun makeRequest(url: String, headers: HashMap<String, String>, request: HttpRequestBase): HttpClientResponse {
    val httpClient = DefaultHttpClient()
    val responseHandler = BasicResponseHandler()
    for ((key, value) in headers) {
        request.setHeader(key, value)
    }
    val responseHeaders = request.getAllHeaders()
    return HttpClientResponse(responseHeaders!!, httpClient.execute(request, responseHandler)!!)
}

public fun delete(url: String, headers: HashMap<String, String>): HttpClientResponse {
    return makeRequest(url, headers, HttpDelete(url))
}

public fun get(url: String, headers: HashMap<String,String>): HttpClientResponse {
    return makeRequest(url, headers, HttpGet(url))
}

public fun postForm(url: String, headers: HashMap<String, String>, fields: ArrayList<BasicNameValuePair>): HttpClientResponse {
    val httpPost = HttpPost(url)
    val entity = UrlEncodedFormEntity(fields, "UTF-8")
    httpPost.setEntity(entity)
    return makeRequest(url, headers, httpPost)
}

data public class HttpClientResponse(val headers: Array<Header>, val body: String)



