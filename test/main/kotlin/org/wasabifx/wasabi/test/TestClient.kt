package org.wasabifx.wasabi.test

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.*
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.wasabifx.wasabi.app.AppServer
import java.util.*

class TestClient(val appServer: AppServer) {

    companion object Methods {
        val GET = "GET"
        val POST = "POST"
        val PUT = "PUT"
        val DELETE = "DELETE"
        val PATCH = "PATCH"
        val HEAD = "HEAD"
        val OPTIONS = "OPTIONS"
        val TRACE = "TRACE"
    }

    fun sendSimpleRequest(url: String, method: String, headers: HashMap<String, String> = hashMapOf()): HttpClientResponse {
        val httpRequest = createHttpRequestInstance(url, method)
        return executeRequest(headers, httpRequest)
    }

    fun sendForm(url: String, method: String, fields: ArrayList<BasicNameValuePair>, headers: HashMap<String, String> = hashMapOf(), chunked: Boolean = false): HttpClientResponse {
        val httpRequest = createHttpRequestInstance(url, method)

        if (httpRequest is HttpEntityEnclosingRequestBase) {
            val entity = UrlEncodedFormEntity(fields, "UTF-8")
            entity.isChunked = chunked
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded")
            httpRequest.entity = entity
        } else {
            throw Exception("Cannot send form with this HTTP method ($method).")
        }

        return executeRequest(headers, httpRequest)
    }

    fun sendForm(url: String, method: String, fields: HashMap<String, String>, headers: HashMap<String, String> = hashMapOf(), chunked: Boolean = false): HttpClientResponse {

        val formFields = ArrayList<BasicNameValuePair>()
        fields.forEach {
            formFields.add(BasicNameValuePair(it.key, it.value))
        }

        return sendForm(url, method, formFields, headers, chunked)
    }

    fun sendJson(url: String, method: String, json: String, headers: HashMap<String, String> = hashMapOf()): HttpClientResponse {
        val httpRequest = createHttpRequestInstance(url, method)

        if (httpRequest is HttpEntityEnclosingRequestBase) {
            httpRequest.setHeader("Content-Type", "application/json")
            httpRequest.entity = StringEntity(json)
        } else {
            throw Exception("Cannot send JSON with this HTTP method ($method).")
        }

        return executeRequest(headers, httpRequest)
    }

    fun sendJson(url: String, method: String, json: Any, headers: HashMap<String, String> = hashMapOf()): HttpClientResponse {
        val objectMapper = ObjectMapper()
        val jsonString = objectMapper.writeValueAsString(json)!!

        return sendJson(url, method, jsonString, headers)
    }

    private fun createHttpRequestInstance(url: String, method: String): HttpRequestBase {
        val fullUrl = "http://localhost:" + appServer.configuration.port.toString() + url

        return when (method) {
            TestClient.GET -> { HttpGet(fullUrl) }
            TestClient.POST -> { HttpPost(fullUrl) }
            TestClient.PUT -> { HttpPut(fullUrl) }
            TestClient.DELETE -> { HttpDelete(fullUrl) }
            TestClient.PATCH -> { HttpPatch(fullUrl) }
            TestClient.HEAD -> { HttpHead(fullUrl) }
            TestClient.OPTIONS -> { HttpOptions(fullUrl) }
            TestClient.TRACE -> { HttpTrace(fullUrl) } // is not supported by wasabi server
            else  -> { throw Exception("Invalid method") }
        }
    }

    private fun executeRequest(headers: HashMap<String, String>, request: HttpRequestBase, cookies: HashMap<String, String> = hashMapOf()): HttpClientResponse {

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
        val body = if (response.entity != null) { EntityUtils.toString(response.entity) } else { null }
        val responseHeaders = response.allHeaders!!

        return HttpClientResponse(responseHeaders, body,
            response.statusLine?.statusCode!!,
            response.statusLine?.reasonPhrase ?: "")
    }
}