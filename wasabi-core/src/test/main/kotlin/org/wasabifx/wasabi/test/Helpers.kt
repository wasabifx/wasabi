package org.wasabifx.wasabi.test

import org.apache.http.Header
import org.apache.http.NameValuePair
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.*
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.junit.After
import org.junit.Before
import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.routing.Route
import java.net.BindException
import java.util.*

open class TestServerContext {
    @Before fun initTest(): Unit {
        TestServer.start()
    }
    @After fun postTest(): Unit {
        TestServer.reset()
    }
}

// TODO: Clean up response access for tests (switch to EasyHttp.jvm)

object TestServer {

    val definedPort: Int = Random().nextInt(30000) + 5000
    var appServer: AppServer = AppServer(AppConfiguration(definedPort))

    fun start() {
        try {
            appServer.start(false)
        } catch (e: BindException) {

        }
    }

    fun stop() {
        appServer.stop()
    }

    fun loadDefaultRoutes() {
        appServer.get("/", { response.send("Root")})
        appServer.get("/first", { response.send("First")})
    }

    val routes: ArrayList<Route>
        get() = appServer.routes

    fun reset() {
        appServer.routes.clear()
        appServer.interceptors.clear()
        appServer.channels.clear()
        appServer.exceptionHandlers.clear()

        appServer.init()
    }
}

private fun makeRequest(headers: HashMap<String, String>, request: HttpRequestBase, cookies: HashMap<String, String> = hashMapOf()): HttpClientResponse {

    val cookieStore = BasicCookieStore()

    for ((key, value) in cookies) {
        val custom_cookie = BasicClientCookie(key, value)
        custom_cookie.path = request.uri?.path
        custom_cookie.domain = "localhost"
        cookieStore.addCookie(custom_cookie)
    }

    val httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build()
    for ((key, value) in headers) {
        request.setHeader(key, value)
    }
    request.setHeader("Connection", "Close")


    val response = httpClient.execute(request)!!

    val body = EntityUtils.toString(response.entity)!!
    val responseHeaders = response.allHeaders!!

    return HttpClientResponse(responseHeaders, body,
            response.statusLine?.statusCode!!,
            response.statusLine?.reasonPhrase ?: "")


}

fun delete(url: String, headers: HashMap<String, String>): HttpClientResponse {
    return makeRequest(headers, HttpDelete(url))
}

fun get(url: String, headers: HashMap<String,String> = hashMapOf(), cookies: HashMap<String, String> = hashMapOf()): HttpClientResponse {

    val requestConfig = RequestConfig.custom()
            .setRedirectsEnabled(false)
            .build()

    val get = HttpGet(url)
    get.config = requestConfig

    return makeRequest(headers, get, cookies)
}

fun post(url: String, headers: HashMap<String,String> = hashMapOf(), postParams: List<NameValuePair>): HttpClientResponse {

    val requestConfig = RequestConfig.custom()
            .setRedirectsEnabled(false)
            .build()

    val post = HttpPost(url)
    post.config = requestConfig
    post.entity = UrlEncodedFormEntity(postParams)
    return makeRequest(headers, post)
}

fun options(url: String, headers: HashMap<String, String> = hashMapOf()): HttpClientResponse {
    return makeRequest(headers, HttpOptions(url))
}


fun postForm(url: String, headers: HashMap<String, String>, fields: ArrayList<BasicNameValuePair>, chunked: Boolean = false): HttpClientResponse {
    val httpPost = HttpPost(url)
    val entity = UrlEncodedFormEntity(fields, "UTF-8")
    entity.isChunked = chunked
    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded")
    httpPost.entity = entity
    return makeRequest(headers, httpPost)
}

data class HttpClientResponse(val headers: Array<Header>, val body: String, val statusCode: Int, val statusDescription: String)


