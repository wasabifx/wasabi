package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.serveFavIconAs
import kotlin.test.assertEquals
import java.io.File

public class FavIconInterceptorSpecs: TestServerContext() {

    spec fun requesting_favicon_should_return_favicon() {

        TestServer.appServer.serveFavIconAs("testData${File.separatorChar}public${File.separatorChar}favicon.ico")

        val response = get("http://localhost:${TestServer.definedPort}/favicon.ico", hashMapOf())

        assertEquals(200, response.statusCode)


    }

}
    