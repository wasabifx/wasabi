package org.wasabi.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpDelete
import org.apache.http.Header
import java.util.HashMap
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.entity.UrlEncodedFormEntity
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.cookie.BasicClientCookie
import org.wasabi.routing.Route
import org.apache.http.util.EntityUtils
import org.junit.Before
import org.junit.After
import org.apache.http.client.methods.HttpOptions
import org.apache.http.params.BasicHttpParams
import org.wasabi.app.AppConfiguration
import java.util.Random
import java.net.BindException

open public class TestServerContext {
    Before fun initServer(): Unit {
        TestServer.start()
        TestServer.reset()
    }
}

object TestServer {

    public val definedPort: Int = Random().nextInt(30000) + 5000
    public var appServer: AppServer = AppServer(AppConfiguration(definedPort))

    public fun start() {
        if (!appServer.isRunning) {
            try {
                appServer.start(false)
            } catch (e: BindException) {
                println("Exception caught")
            }
        }
    }

    public fun stop() {
        appServer.stop()
    }

    public fun loadDefaultRoutes() {
        appServer.get("/", { response.send("Root")})
        appServer.get("/first", { response.send("First")})
    }

    public val routes: ArrayList<Route>
        get() = appServer.routes

    public fun reset() {
        appServer.routes.clear()
    }
}

private fun makeRequest(headers: HashMap<String, String>, request: HttpRequestBase): org.wasabi.test.HttpClientResponse {
    val httpClient = DefaultHttpClient()
    for ((key, value) in headers) {
        request.setHeader(key, value)
    }
    request.setHeader("Connection", "Close")
    val cookie = BasicClientCookie("someCookie", "someCookieValue")
    cookie.setPath(request.getURI()?.getPath())
    cookie.setDomain("localhost")
    val cookieStore = httpClient.getCookieStore()
    cookieStore?.addCookie(cookie)

    val response = httpClient.execute(request)!!

    val body = EntityUtils.toString(response.getEntity())!!
    val responseHeaders = response.getAllHeaders()!!

    return HttpClientResponse(responseHeaders, body,
            response.getStatusLine()?.getStatusCode()!!,
            response.getStatusLine()?.getReasonPhrase() ?: "")


}

public fun delete(url: String, headers: HashMap<String, String>): org.wasabi.test.HttpClientResponse {
    return makeRequest(headers, HttpDelete(url))
}

public fun get(url: String, headers: HashMap<String,String> = hashMapOf()): org.wasabi.test.HttpClientResponse {
    val get = HttpGet(url)
    val params = BasicHttpParams()
    params.setParameter("http.protocol.handle-redirects",false)
    get.setParams(params)
    return makeRequest(headers, get)
}

public fun options(url: String, headers: HashMap<String, String> = hashMapOf()): HttpClientResponse {
    return makeRequest(headers, HttpOptions(url))
}


public fun postForm(url: String, headers: HashMap<String, String>, fields: ArrayList<BasicNameValuePair>, chunked: Boolean = false): org.wasabi.test.HttpClientResponse {
    val httpPost = HttpPost(url)
    val entity = UrlEncodedFormEntity(fields, "UTF-8")
    entity.setChunked(chunked)
    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded")
    httpPost.setEntity(entity)
    return org.wasabi.test.makeRequest(headers, httpPost)
}

data public class HttpClientResponse(val headers: Array<Header>, val body: String, val statusCode: Int, val statusDescription: String)


