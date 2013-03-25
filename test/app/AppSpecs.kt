package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.app.AppServer
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import kotlin.test.assertEquals
import org.apache.http.impl.client.BasicResponseHandler

public class AppSpecs {

    spec(timeout=9000) fun requesting_an_existing_resource_should_return_it() {

        val appServer = AppServer()

        appServer.routes.get("/", { request, response -> response.send("Hello")})

        appServer.start()

        val httpClient = DefaultHttpClient()

        val responseHandler = BasicResponseHandler()

        val httpGet = HttpGet("http://localhost:3000")

        val response = httpClient.execute(httpGet, responseHandler)

        assertEquals("Hello", response)


    }


}


