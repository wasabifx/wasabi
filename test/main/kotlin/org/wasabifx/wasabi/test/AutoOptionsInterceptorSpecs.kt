package org.wasabifx.wasabi.test

import org.junit.Test as spec
import org.wasabifx.wasabi.interceptors.enableAutoOptions
import kotlin.test.assertEquals
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.interceptors.disableAutoOptions
import org.wasabifx.wasabi.interceptors.AutoOptionsInterceptor
import org.junit.Ignore

class AutoOptionsInterceptorSpecs : TestServerContext() {

    @spec fun testing_auto_options_shutdown () {
        TestServer.appServer.enableAutoOptions()

        assertEquals(1, TestServer.appServer.interceptors.count { it.interceptor is AutoOptionsInterceptor })

        TestServer.appServer.disableAutoOptions()

        assertEquals(0, TestServer.appServer.interceptors.count { it.interceptor is AutoOptionsInterceptor })

    }

    @spec fun auto_options_should_return_all_methods_available_for_a_specific_resource () {
        TestServer.appServer.get("/person", {})
        TestServer.appServer.post("/person", {})
        TestServer.appServer.post("/customer", {})
        TestServer.appServer.enableAutoOptions()

        val response = options("http://localhost:${TestServer.definedPort}/person")

        val allowHeader = response.headers.filter { it.name == "Allow"}.first()

        assertEquals("GET,POST", allowHeader.value)

        TestServer.appServer.disableAutoOptions()
    }

    @Ignore
    @spec fun with_auto_options_disabled_options_should_return_method_not_allowed () {

        TestServer.appServer.get("/person", {})
        TestServer.appServer.enableAutoOptions()

        val response = options("http://localhost:${TestServer.definedPort}/person")

        assertEquals(StatusCodes.MethodNotAllowed.code, response.statusCode)

        TestServer.appServer.disableAutoOptions()
    }

}
