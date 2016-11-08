 package org.wasabifx.wasabi.test

import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.cookie.ServerCookieEncoder
import org.junit.Test as spec
 import kotlin.test.assertEquals
import org.junit.Ignore
import org.wasabifx.wasabi.interceptors.enableSessionSupport
import kotlin.test.assertTrue

 class SessionManagementInterceptorSpecs: TestServerContext() {

    class CustomSession(val name: String) {

    }
    @spec fun should_have_same_session_between_multiple_requests() {

        TestServer.appServer.enableSessionSupport()
        TestServer.appServer.get("/test_session", {

        })

        // Make sure session id stays consistent between requests.
        val response = get("http://localhost:${TestServer.definedPort}/test_session")
        var cookieString = ""
        response.headers.forEach {
            if (it.name == "Set-Cookie") {
                cookieString = it.value
            }
        }

        assertTrue(cookieString.length > 0)

        /*
         * cookieString value is this: _sessionID=057907cf-9a08-48ff-9fbe-43c8d5ebb0fb; Path=/; Domain=localhost
         * so at first we split by ; so we will get _sessionID=057907cf-9a08-48ff-9fbe-43c8d5ebb0fb
         * and then split by =
         */
        val (cookieName,sessionId) = cookieString.split(";")[0].split("=")

        // Set session cookie as you would expect the client to do...
        val response2 = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf(), hashMapOf(cookieName to sessionId))

        // Check it comes back and matches original.
        var cookieString2 = ""
        response2.headers.iterator().forEach {
            if (it.name == "Set-Cookie") {
                cookieString2 = it.value
            }
        }
        assertEquals(cookieString, cookieString2)
    }

     @spec fun cookie_should_have_root_path() {
         TestServer.appServer.enableSessionSupport()
         TestServer.appServer.get("/test-session", {
             response.send("Test", "text/plain")
         })

         // Make sure session id stays consistent between requests.
         val response = get("http://localhost:${TestServer.definedPort}/test-session", hashMapOf())

         assertTrue(response.headers.filter { it.name == "Set-Cookie" }.count() > 0)

         response.headers.forEach {
             if (it.name == "Set-Cookie") {
                 val parts = it.value.split(";")
                 assertTrue(parts.filter { it.trim() == "Path=/" }.count() > 0)
             }
         }
     }

}
