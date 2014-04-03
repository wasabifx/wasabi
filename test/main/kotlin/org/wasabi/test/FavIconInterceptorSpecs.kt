package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.serveFavIconAs
import kotlin.test.assertEquals

public class FavIconInterceptorSpecs: TestServerContext() {

    spec fun requesting_favicon_should_return_favicon() {

        TestServer.appServer.serveFavIconAs("/public/favicon.ico")

        val response = get("http://localhost:${TestServer.definedPort}/favicon.ico", hashMapOf())

        assertEquals(200, response.statusCode)


    }

}
    