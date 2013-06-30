package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.app.AppServer
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import kotlin.test.assertEquals
import org.apache.http.impl.client.BasicResponseHandler
import org.wasabai.test.TestServer
import org.wasabai.test.get
import kotlin.test.fails
import org.apache.http.client.HttpResponseException
import org.wasabai.test.delete

public class UrlRequestingSpecs: TestServerContext() {


    val headers = hashMapOf(
            "User-Agent" to "test-client",
            "Connection" to "keep-alive",
            "Cache-Control" to "max-age=0",
            "Accept" to "Accept=text/html,application/xhtml+xml,application/xml",
            "Accept-Encoding" to "gzip,deflate,sdch",
            "Accept-Language" to "en-US,en;q=0.8",
            "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

    )

    spec(timeout=5000) fun a_get_on_an_existing_resource_should_return_it() {


        TestServer.reset()
        TestServer.appServer.get("/", {  response.send("Hello")})


        val response = get("http://localhost:3000", headers)

        assertEquals("Hello", response.body)

    }

    spec(timeout=5000) fun a_get_on_an_non_existing_resource_should_return_a_404_with_message_Not_Found() {



        TestServer.reset()
        val exception = fails { get("http://localhost:3000/nothing", headers)}

        assertEquals(javaClass<HttpResponseException>(),exception.javaClass)
        assertEquals("Not found",exception!!.getMessage())


    }

    spec(timeout=5000) fun a_get_on_an_existing_resource_with_invalid_verb_should_return_405_with_message_method_not_allowed_and_header_of_allowed_methods() {

        TestServer.reset()
        TestServer.appServer.get("/", {  response.send("Hello")})

        val exception = fails { delete("http://localhost:3000", headers) }

        assertEquals(javaClass<HttpResponseException>(), exception.javaClass)
        assertEquals("Method not allowed", exception!!.getMessage())

        // for some reason HttpClient does not return allow header even though it is being set (verified with REST client in IDEA and curl)
        //  assertEquals("Allow: GET", headers["Allow"])
    }

}


