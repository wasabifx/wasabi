package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.test.TestServer
import org.wasabi.test.get
import kotlin.test.assertEquals
import org.wasabi.interceptors.parseContentNegotiationHeaders


public class ConnegSpecs: TestServerContext() {

    spec fun when_() {
        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "keep-alive",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        TestServer.appServer.parseContentNegotiationHeaders() {
            onAcceptHeader()
        }

        TestServer.appServer.get("/customer/",
                {
                    response.send("shit in json!")
                })



        val response = get("http://localhost:3000/customer/", headers)

        assertEquals(response.body, "shit in json!")



    }
}