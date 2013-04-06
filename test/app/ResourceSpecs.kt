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
import org.wasabi.routing.Routes
import org.wasabai.test.delete

public class ResourceSpecs {

    spec(timeout=1000) fun a_get_on_an_existing_resource_should_return_it() {


        Routes.get("/", {  response.send("Hello")})

        TestServer.start()

        val response = get("http://localhost:3000")

        assertEquals("Hello", response)

        TestServer.stop()

    }

    spec(timeout=1000) fun a_get_on_an_non_existing_resource_should_return_a_404_with_message_Not_Found() {

        TestServer.start()

        val exception = fails { get("http://localhost:3000/nothing")}

        assertEquals(javaClass<HttpResponseException>(),exception.javaClass)
        assertEquals("Not found",exception!!.getMessage())

        TestServer.stop()

    }

    spec(timeout=1000) fun a_get_on_an_existing_resource_with_invalid_verb_should_return_405_with_message_method_not_allowed_and_header_of_allowed_methods() {

        Routes.get("/", {  response.send("Hello")})

        TestServer.start()

        val exception = fails { delete("http://localhost:3000") }

        assertEquals(javaClass<HttpResponseException>(), exception.javaClass)
        assertEquals("Method not allowed", exception!!.getMessage())

        TestServer.stop()
    }

}


