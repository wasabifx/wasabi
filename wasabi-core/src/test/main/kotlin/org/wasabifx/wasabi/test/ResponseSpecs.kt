package org.wasabifx.wasabi.test

import org.wasabifx.wasabi.protocol.http.Cookie
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test as spec

class ResponseSpecs: TestServerContext() {

    @spec fun cookies_must_be_encoded_with_ServerCookieEncoder() {
        TestServer.reset()
        TestServer.appServer.get("/test", {
            val cookie = Cookie("testing", "lipsum")
            cookie.setPath("/test")
            cookie.setDomain("localhost")
            cookie.isSecure = true
            cookie.isHttpOnly = true
            response.setCookie(cookie)
            response.send("test", "plain/text")
        })

        val response = TestClient(TestServer.appServer).sendSimpleRequest("/test", "GET")
        val cookieHeaders = response.headers.filter { it.name == "Set-Cookie" }
        assertTrue(cookieHeaders.count() > 0)

        val cookieString = cookieHeaders.first().value

        assertEquals("testing=lipsum; Path=/test; Domain=localhost; Secure; HTTPOnly", cookieString)
    }

}