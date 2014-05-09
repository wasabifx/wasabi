package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.serveStaticFilesFromFolder
import kotlin.test.assertEquals
import java.io.File

public class StaticFileInterceptorSpecs: TestServerContext() {

    spec fun requesting_an_existing_static_file_should_return_the_file() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/test.html", hashMapOf())


        assertEquals("<!DOCTYPE html><head><title></title></head><body>This is an example static file</body></html>", response.body)
    }

    spec fun requesting_an_non_existing_static_file_should_404() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/test1.html", hashMapOf())


        assertEquals("Not Found", response.statusDescription)
        assertEquals(404, response.statusCode)
    }


}