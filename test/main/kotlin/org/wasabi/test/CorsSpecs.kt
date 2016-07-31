package org.wasabi.test

import io.netty.handler.codec.http.HttpMethod
import org.junit.Test
import org.wasabi.interceptors.*
import org.wasabi.protocol.http.CORSEntry
import kotlin.test.assertEquals

fun getHeader(headerName: String, response: HttpClientResponse): String? {
    return response.headers
            .filter { it.getName() == headerName}
            .firstOrNull()?.value
}

fun getCORSAllowHeader(response: HttpClientResponse): String? {
    return getHeader("Access-Control-Request-Method", response)
}

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
        assertEquals("*", response.headers.filter { it.getName() == "Access-Control-Allow-Origin"}.first().getValue())
        assertEquals("Origin, X-Requested-With, Content-Type, Accept", response.headers.filter { it.getName() == "Access-Control-Allow-Headers"}.first().getValue())
        assertEquals("GET,POST,PUT", response.headers.filter { it.getName() == "Access-Control-Request-Method"}.first().getValue())

        val response2 = options("http://localhost:${TestServer.definedPort}/customer")
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

    @Test fun cors_should_return_correct_header_on_globally_enabled_cors () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/person", {})
        TestServer.appServer.patch("/person", {})
        TestServer.appServer.head("/person", {})
        TestServer.appServer.enableCORSGlobally()

        val response = options("http://localhost:${TestServer.definedPort}/person")
        assertEquals("GET,POST,PATCH,HEAD", getCORSAllowHeader(response))

        TestServer.appServer.disableCORS()
    }

    @Test fun cors_should_return_correct_header_on_custom_cors_rules () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/person", {})
        TestServer.appServer.patch("/person", {})

        TestServer.appServer.get("/personalization", {})

        TestServer.appServer.get("/account", {})
        TestServer.appServer.post("/account", {})
        TestServer.appServer.patch("/account", {})

        TestServer.appServer.get("/thread", {})
        TestServer.appServer.post("/thread", {})
        TestServer.appServer.delete("/thread", {})

        TestServer.appServer.enableCORS(
            arrayListOf(
                CORSEntry(path = "/person.*", methods = CORSEntry.ALL_AVAILABLE_METHODS),
                CORSEntry(path = "/account.*", methods = setOf(HttpMethod.GET, HttpMethod.POST)),
                CORSEntry(path = "/thread.*", methods = CORSEntry.NO_METHODS)
            )
        )

        val personResponse = options("http://localhost:${TestServer.definedPort}/person")
        assertEquals("GET,POST,PATCH", getCORSAllowHeader(personResponse))

        val personalizationResponse = options("http://localhost:${TestServer.definedPort}/personalization")
        assertEquals("GET", getCORSAllowHeader(personalizationResponse))

        val accountResponse = options("http://localhost:${TestServer.definedPort}/account")
        assertEquals("GET,POST", getCORSAllowHeader(accountResponse))

        val threadResponse = options("http://localhost:${TestServer.definedPort}/thread")
        assertEquals(null, getCORSAllowHeader(threadResponse))

        TestServer.appServer.disableCORS()
    }
}