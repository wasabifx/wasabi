 package org.wasabifx.wasabi.test

import org.junit.Test as spec
 import kotlin.test.assertEquals
import org.wasabifx.wasabi.interceptors.enableSessionSupport
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

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

        val (cookieName,sessionId) = this.getCookieNameAndSessionId(cookieString)

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

     @spec fun session_should_support_multiple_variables() {
        TestServer.appServer.enableSessionSupport()
        TestServer.appServer.get("/set-session", {
            request.session?.set("test", "lipsum1")
            response.send("OK", "text/plain")
        })

        TestServer.appServer.get("/set-session2", {
            request.session?.set("test2", "lipsum2")
            response.send("OK", "text/plain")
        })

        TestServer.appServer.get("/get-session", {
            response.send(request.session?.get("test") ?: "not found", "text/plain")
        })

        TestServer.appServer.get("/get-session2", {
            response.send(request.session?.get("test2") ?: "not found", "text/plain")
        })

        val response = get("http://localhost:${TestServer.definedPort}/set-session")
        val cookieString = response.headers
            .filter { it.name == "Set-Cookie" }
            .map { it.value }
            .firstOrNull()

        assertNotNull(cookieString)

        cookieString?.let {
            val (cookieName, sessionId) = this.getCookieNameAndSessionId(cookieString)

            get("http://localhost:${TestServer.definedPort}/set-session2", hashMapOf(), hashMapOf(cookieName to sessionId))

            assertEquals("lipsum1", get("http://localhost:${TestServer.definedPort}/get-session", hashMapOf(), hashMapOf(cookieName to sessionId)).body)
            assertEquals("lipsum2", get("http://localhost:${TestServer.definedPort}/get-session2", hashMapOf(), hashMapOf(cookieName to sessionId)).body)
        }
     }

     private fun getCookieNameAndSessionId(headerValue: String): List<String> {
         return headerValue.split(";")[0].split("=")
     }
}
