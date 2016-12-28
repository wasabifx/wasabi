package org.wasabifx.wasabi.test

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.wasabifx.wasabi.routing.routeHandler
import java.nio.charset.Charset
import java.util.*
import kotlin.test.assertEquals
import org.junit.Test as spec

/**
 * Created by swishy on 25/11/15.
 */
class BodyParametersSpecs : TestServerContext() {

    val createMember = routeHandler {
        var memberName = request.bodyParams["memberName"]
        assert(memberName == "bob")
    }

    @spec fun requests_should_have_isolated_body_params() {

        TestServer.appServer.post("/params", {
            response.send(request.bodyParams.size.toString())
        })

        val urlParameters = ArrayList<NameValuePair>()
        urlParameters.add(BasicNameValuePair("foo", "bar"))

        val urlParameters2 = ArrayList<NameValuePair>()
        urlParameters2.add(BasicNameValuePair("bar", "baz"))

        val response = post("http://localhost:${TestServer.definedPort}/params", hashMapOf(), urlParameters)

        assert(response.body?.contains("1") ?: false)

        val response2 = post("http://localhost:${TestServer.definedPort}/params", hashMapOf(), urlParameters2)

        assert(response2.body?.contains("1") ?: false)

    }

    @spec fun body_params_present_on_request() {

        TestServer.appServer.post("/body", createMember)

        var urlParameters3 = ArrayList<NameValuePair>()
        urlParameters3.add(BasicNameValuePair("memberName", "bob"))

        post("http://localhost:${TestServer.definedPort}/body", hashMapOf(), urlParameters3)
    }

    @spec fun body_is_populated_even_with_invalid_content() {

        val testValue = "FOO"

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Charset" to "utf-8"
        )

        TestServer.appServer.post("/raw", {
            response.send(String(request.body, Charset.forName("UTF-8")))
        })

        val client = OkHttpClient()
        val body = RequestBody.create(null, "FOO")
        val request = Request.Builder()
                .url("http://localhost:${TestServer.definedPort}/raw")
                .header("Accept", "application/json")
                .header("Accept-Encoding", "gzip, deflate")
                .method("POST", body)
                .build()

        val response = client.newCall(request).execute()
        assertEquals(testValue, response.body().string())
    }
}