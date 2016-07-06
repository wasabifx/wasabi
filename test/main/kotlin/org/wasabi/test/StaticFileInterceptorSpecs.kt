package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.serveStaticFilesFromFolder
import kotlin.test.assertEquals
import java.io.File

public class StaticFileInterceptorSpecs: TestServerContext() {

    @spec fun requesting_an_existing_static_file_should_return_the_file() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/test.html", hashMapOf())
        val response1 = get("http://localhost:${TestServer.definedPort}/error.html", hashMapOf())


        assertEquals("<!DOCTYPE html><head><title></title></head><body>This is an example static file</body></html>", response.body)
        assertEquals("<!DOCTYPE html><head><title></title></head><body>Standard Error File</body></html>", response1.body)
    }

    @spec fun requesting_an_existing_static_file_should_return_correct_content_type() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/style.css", hashMapOf())


        assertEquals("text/css", response.headers.first( { it.getName() == "Content-Type"}).getValue())
    }

    @spec fun requesting_an_non_existing_static_file_should_404() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/test1.html", hashMapOf())


        assertEquals("Not Found", response.statusDescription)
        assertEquals(404, response.statusCode)
    }

    @spec fun requesting_an_existing_static_directory_should_go_to_next() {
        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")
        TestServer.loadDefaultRoutes()
        val response = get("http://localhost:${TestServer.definedPort}/", hashMapOf())
        assertEquals("Root", response.body)
    }

    @spec fun requesting_an_existing_static_directory_should_serve_when_default_file_is_turn_on() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public", true, "test.html")

        val response = get("http://localhost:${TestServer.definedPort}/", hashMapOf())

        assertEquals("<!DOCTYPE html><head><title></title></head><body>This is an example static file</body></html>", response.body)

        TestServer.reset()
    }
}