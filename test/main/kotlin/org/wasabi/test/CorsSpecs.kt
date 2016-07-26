package org.wasabi.test

import org.junit.Test
import org.wasabi.interceptors.*
import org.wasabi.protocol.http.CORSEntry
import kotlin.test.assertEquals

class CorsSpecs : TestServerContext(){

    @Test fun testing_cors_enabled () {

        //TestServer.appServer.enableAutoOptions()
        TestServer.appServer.enableCORS(arrayListOf(CORSEntry()))

        assertEquals(1, TestServer.appServer.interceptors.count { it.interceptor is CORSInterceptor })

        TestServer.appServer.disableCORS()
    }

    @Test fun cors_should_only_work_on_declared_routes () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/person", {})

        TestServer.appServer.post("/customer", {})
        TestServer.appServer.enableCORS(arrayListOf(CORSEntry(path = "/person")))

        val response = options("http://localhost:${TestServer.definedPort}/person")
        assertEquals("GET,POST", response.headers.filter { it.getName() == "Allow"}.first().getValue())
        assertEquals("*", response.headers.filter { it.getName() == "Access-Control-Allow-Origin"}.first().getValue())
        assertEquals("Origin, X-Requested-With, Content-Type, Accept", response.headers.filter { it.getName() == "Access-Control-Allow-Headers"}.first().getValue())
        assertEquals("GET,POST", response.headers.filter { it.getName() == "Access-Control-Request-Method"}.first().getValue())

        val response2 = options("http://localhost:${TestServer.definedPort}/customer")
        assertEquals(405, response2.statusCode)

        TestServer.appServer.disableCORS()
    }

    @Test fun cors_should_work_on_all_when_globally_enabled () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/person", {})
        TestServer.appServer.put("/person", {})

        TestServer.appServer.post("/customer", {})
        TestServer.appServer.enableCORSGlobally()

        val response = options("http://localhost:${TestServer.definedPort}/person")
        assertEquals("GET,POST,PUT", response.headers.filter { it.getName() == "Allow"}.first().getValue())
        assertEquals("*", response.headers.filter { it.getName() == "Access-Control-Allow-Origin"}.first().getValue())
        assertEquals("Origin, X-Requested-With, Content-Type, Accept", response.headers.filter { it.getName() == "Access-Control-Allow-Headers"}.first().getValue())
        assertEquals("GET,POST,PUT", response.headers.filter { it.getName() == "Access-Control-Request-Method"}.first().getValue())

        val response2 = options("http://localhost:${TestServer.definedPort}/customer")
        assertEquals("POST", response2.headers.filter { it.getName() == "Allow"}.first().getValue())
        assertEquals("*", response2.headers.filter { it.getName() == "Access-Control-Allow-Origin"}.first().getValue())
        assertEquals("Origin, X-Requested-With, Content-Type, Accept", response2.headers.filter { it.getName() == "Access-Control-Allow-Headers"}.first().getValue())
        assertEquals("POST", response2.headers.filter { it.getName() == "Access-Control-Request-Method"}.first().getValue())

        TestServer.appServer.disableCORS()
    }

    @Test fun cors_should_fail_when_not_enabled () {
        TestServer.appServer.post("/customer", {})

        val response = options("http://localhost:${TestServer.definedPort}/customer")

        assertEquals(405, response.statusCode)
    }

    @Test fun cors_should_return_correct_header_on_non_option_request () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/customer", {})
        TestServer.appServer.enableCORS(arrayListOf(CORSEntry(path = "/person")))

        val response = options("http://localhost:${TestServer.definedPort}/person")

        val getResponse = get("http://localhost:${TestServer.definedPort}/person")
        assertEquals("/person", getResponse.headers.filter { it.getName() == "Access-Control-Allow-Origin"}.first().getValue())

        TestServer.appServer.disableCORS()
    }
}