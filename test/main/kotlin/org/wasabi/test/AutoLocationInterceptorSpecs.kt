package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.enableAutoOptions
import kotlin.test.assertEquals
import org.wasabi.http.StatusCodes
import org.wasabi.interceptors.disableAutoOptions
import org.wasabi.interceptors.AutoOptionsInterceptor
import org.junit.Ignore
import org.wasabi.interceptors.enableAutoLocation


public class AutoLocationInterceptorSpecs : TestServerContext() {
    @spec fun with_auto_location_interceptor_enabled_when_setting_response_as_created_and_resourceId_it_should_return_location_on_post () {
        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*"
        )
        TestServer.appServer.enableAutoLocation()
        TestServer.appServer.post("/person", {
            response.resourceId = "20"
            response.setStatus(StatusCodes.Created)
        })
        val response = postForm("http://localhost:${TestServer.definedPort}/person", headers, arrayListOf())
        assertEquals("http://localhost:${TestServer.definedPort}/person/20", response.headers.filter { it.getName() == "Location" }.firstOrNull()?.getValue())
    }
}
