package org.wasabi.test

import org.wasabi.app.AppServer
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
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
import org.apache.http.impl.cookie.BasicClientCookie
import org.wasabi.routing.Route
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import javax.swing.text.html.parser.Entity

object TestServer {

    public val appServer: AppServer = AppServer()

    public fun start() {
        if (!appServer.isRunning) {
            appServer.start(false)
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

private fun makeRequest(headers: HashMap<String, String>, request: HttpRequestBase): HttpClientResponse {
    val httpClient = DefaultHttpClient()
    for ((key, value) in headers) {
        request.setHeader(key, value)
    }
    val cookie = BasicClientCookie("someCookie", "someCookieValue")
    cookie.setPath(request.getURI()?.getPath())
    cookie.setDomain("localhost")
    val cookieStore = httpClient.getCookieStore()
    cookieStore?.addCookie(cookie)

    val response = httpClient.execute(request)!!

    val body = EntityUtils.toString(response.getEntity())!!
    return HttpClientResponse(response.getAllHeaders()!!, body)
}

public fun delete(url: String, headers: HashMap<String, String>): HttpClientResponse {
    return makeRequest(headers, HttpDelete(url))
}

public fun get(url: String, headers: HashMap<String,String>): HttpClientResponse {
    return makeRequest(headers, HttpGet(url))
}

public fun postForm(url: String, headers: HashMap<String, String>, fields: ArrayList<BasicNameValuePair>, chunked: Boolean = false): HttpClientResponse {
    val httpPost = HttpPost(url)
    val entity = UrlEncodedFormEntity(fields, "UTF-8")
    entity.setChunked(chunked)
    httpPost.setEntity(entity)
    return makeRequest(headers, httpPost)
}

data public class HttpClientResponse(val headers: Array<Header>, val body: String)


