package org.wasabifx.wasabi.test

import kotlin.test.assertEquals
import org.junit.Test as spec

public class UrlRequestingSpecs: TestServerContext() {


    val headers = hashMapOf(
            "User-Agent" to "test-client",
            "Cache-Control" to "max-age=0",
            "Accept" to "text/html,application/xhtml+xml,application/xml",
            "Accept-Encoding" to "gzip,deflate,sdch",
            "Accept-Language" to "en-US,en;q=0.8",
            "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

    )

    @spec fun a_get_on_an_existing_resource_should_return_it() {

        TestServer.appServer.get("/hello", {  response.send("Hello")})


        val response = get("http://localhost:${TestServer.definedPort}/hello", headers)

        assertEquals("Hello", response.body)

    }

    @spec fun a_get_on_an_non_existing_resource_should_return_a_404_with_message_Not_Found() {


        val response = get("http://localhost:${TestServer.definedPort}/nothing", headers)

        assertEquals(404, response.statusCode)
        assertEquals("Not Found", response.statusDescription)

    }

    @spec fun a_get_on_an_existing_resource_with_invalid_verb_should_return_405_with_message_method_not_allowed_and_header_of_allowed_methods() {

        TestServer.appServer.get("/hello", {  response.send("Hello")})

        val response = delete("http://localhost:${TestServer.definedPort}/hello", headers)

        assertEquals(405, response.statusCode)
        assertEquals("Method Not Allowed", response.statusDescription)
        // TODO: Fix headers on client side .. (get helper)
        //assertEquals("Allow: GET", headers["Allow"])
    }

}


