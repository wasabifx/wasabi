package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.app.AppServer
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import kotlin.test.assertEquals
import org.apache.http.impl.client.BasicResponseHandler
import org.wasabai.test.TestServer
import org.wasabai.test.Get
import kotlin.test.fails
import org.apache.http.client.HttpResponseException

public class ResourceSpecs {

    spec(timeout=1000) fun a_get_on_an_existing_resource_should_return_it() {


        TestServer.appServer.routes.get("/", { request, response -> response.send("Hello")})

        TestServer.start()

        val response = Get("http://localhost:3000")

        assertEquals("Hello", response)

        TestServer.stop()

    }

    spec(timeout=1000) fun a_get_on_an_non_existing_resource_should_return_a_404_with_message_Not_Found() {

        TestServer.start()

        val exception = fails({Get("http://localhost:3000/nothing")})

        assertEquals(javaClass<HttpResponseException>(),exception.javaClass)
        assertEquals("Not Found",exception!!.getMessage())

        TestServer.stop()

    }

}


