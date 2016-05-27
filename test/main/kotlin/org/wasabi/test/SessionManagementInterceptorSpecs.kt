 package org.wasabi.test

import org.junit.Test as spec
 import kotlin.test.assertEquals
 import org.wasabi.interceptors.enableSessionSupport
 import org.junit.Ignore
import org.wasabi.protocol.http.Session

 public class SessionManagementInterceptorSpecs: TestServerContext() {

    class CustomSession(val name: String) {

    }
    @spec fun should_have_same_session_between_multiple_requests() {

        TestServer.appServer.enableSessionSupport()
        TestServer.appServer.get("/test_session", {

        })

        // Make sure session id stays consistent between requests.
        var response = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf())
        var session_id = response.headers.get(2)

        Thread.sleep(2000)

        // Set session cookie as you would expect the client to do...
        val response2 = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf(), hashMapOf(Pair(session_id.value.split("=").first(), session_id.value.split("=").last())))

        // Check it comes back and matches original.
        assertEquals(session_id.value, response2.headers.get(2).value)
    }

}
    