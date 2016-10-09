package org.wasabifx.wasabi.test

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.wasabifx.wasabi.interceptors.serveStaticFilesFromFolder
import org.junit.Test as spec
import kotlin.test.assertEquals
import java.io.File

class StaticFileInterceptorSpec: Spek({

    given("a server with StaticFileInterceptor configured") {
        beforeEach {
            TestServer.start()
        }
        afterEach {
            TestServer.reset()
        }

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")
        on("requesting an existing static file") {
            val response = get("http://localhost:${TestServer.definedPort}/test.html", hashMapOf())
            it("should serve the file") {
                assertEquals("<!DOCTYPE html><head><title></title></head><body>This is an example static file</body></html>", response.body)
            }
        }
    }
})
class StaticFileInterceptorSpecs: TestServerContext() {


    @spec fun requesting_an_existing_static_file_should_return_the_file() {




    }

    @spec fun requesting_an_existing_static_file_should_return_correct_content_type() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/style.css", hashMapOf())


        assertEquals("text/css", response.headers.first( { it.name == "Content-Type"}).value)
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

    @spec fun requesting_an_existing_static_file_with_additional_url_params_should_return_the_file() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/test.html?v=3", hashMapOf())
        val response1 = get("http://localhost:${TestServer.definedPort}/error.html?test=test", hashMapOf())


        assertEquals("<!DOCTYPE html><head><title></title></head><body>This is an example static file</body></html>", response.body)
        assertEquals("<!DOCTYPE html><head><title></title></head><body>Standard Error File</body></html>", response1.body)
    }

    @spec fun requesting_an_file_with_spaces_in_filename_should_work_correctly() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/file%20with%20spaces%20in%20filename.txt", hashMapOf())

        assertEquals("lorem ipsum", response.body.trim())
    }

    @spec fun requesting_an_file_outside_of_static_folder_should_raise_internal_server_error() {

        TestServer.appServer.serveStaticFilesFromFolder("testData${File.separatorChar}public")

        val response = get("http://localhost:${TestServer.definedPort}/..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2Fetc%2Fpasswd", hashMapOf())

        assertEquals("Internal Server Error", response.body.trim())
    }
}