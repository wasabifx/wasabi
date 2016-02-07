 package org.wasabi.test

import org.junit.Test as spec
 import kotlin.test.assertEquals
 import org.wasabi.interceptors.enableSessionSupport
 import org.junit.Ignore
import org.wasabi.http.Session

 public class SessionManagementInterceptorSpecs: TestServerContext() {

    class CustomSession(val name: String) {

    }
    @Ignore("Still broken") @spec fun should_extend_session_expiry_on_multiple_requests() {

        TestServer.appServer.enableSessionSupport()
        TestServer.appServer.get("/test_session", {
            var foo = request.session
        })

        var response = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf())

        Thread.sleep(2000)

        val response2 = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf())

        assertEquals("Joe", response.body)
    }

}
    