package org.wasabi.test

import org.junit.Test
import org.wasabi.interceptors.*
import org.wasabi.protocol.http.CORSEntry
import kotlin.test.assertEquals

class CorsSpecs : TestServerContext(){

    @Test fun testing_cors_enabled () {

        //TestServer.appServer.enableAutoOptions()
        TestServer.appServer.enableCORS(arrayListOf(CORSEntry()))

        // TODO Investigate....
        assertEquals(2, TestServer.appServer.interceptors.count { it.interceptor is CORSInterceptor })
    }

    @Test fun cors_should_return_access_control_allow_origin () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/person", {})
        TestServer.appServer.post("/customer", {})
        //TestServer.appServer.enableAutoOptions()
        TestServer.appServer.enableCORS(arrayListOf(CORSEntry()))
        // TestServer.appServer.enableCORSGlobally()

        val response = options("http://localhost:${TestServer.definedPort}/person")
        assertEquals("GET, POST", response.headers.filter { it.getName() == "Allow"}.first().getValue())

        TestServer.appServer.disableAutoOptions()
    }
}