package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.app.AppServer
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import kotlin.test.assertEquals
import org.apache.http.impl.client.BasicResponseHandler
import org.wasabi.test.TestServer
import org.wasabi.test.get
import kotlin.test.fails
import org.apache.http.client.HttpResponseException
import org.wasabi.test.delete

public class UrlRequestingSpecs: TestServerContext() {


    val headers = hashMapOf(
            "User-Agent" to "test-client",
            "Cache-Control" to "max-age=0",
            "Accept" to "text/html,application/xhtml+xml,application/xml",
            "Accept-Encoding" to "gzip,deflate,sdch",
            "Accept-Language" to "en-US,en;q=0.8",
            "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

    )

    spec(timeout=5000) fun a_get_on_an_existing_resource_should_return_it() {

        TestServer.appServer.get("/", {  response.send("Hello")})


        val response = get("http://localhost:${TestServer.definedPort}", headers)

        assertEquals("Hello", response.body)

    }

    spec(timeout=5000) fun a_get_on_an_non_existing_resource_should_return_a_404_with_message_Not_Found() {


        val response = get("http://localhost:${TestServer.definedPort}/nothing", headers)

        assertEquals(404, response.statusCode)
        assertEquals("Not Found", response.statusDescription)

    }

    spec(timeout=5000) fun a_get_on_an_existing_resource_with_invalid_verb_should_return_405_with_message_method_not_allowed_and_header_of_allowed_methods() {

        TestServer.appServer.get("/", {  response.send("Hello")})

        val response = delete("http://localhost:${TestServer.definedPort}", headers)

        assertEquals(405, response.statusCode)
        assertEquals("Method Not Allowed", response.statusDescription)
        // TODO: Fix headers on client side .. (get helper)
        //assertEquals("Allow: GET", headers["Allow"])
    }

}


