package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.serveStaticFilesFromFolder
import kotlin.test.assertEquals
import org.wasabi.interceptors.serveErrorsFromFolder
import org.wasabi.http.StatusCodes

public class FileBasedErrorInterceptorSpecs : TestServerContext() {

    spec fun when_an_error_occurs_and_corresponding_error_file_exists_it_should_serve_it() {

        TestServer.reset()
        TestServer.appServer.serveErrorsFromFolder("/public")

        val response = get("http://localhost:3000/notvalid", hashMapOf())

        assertEquals(404, response.statusCode)
        assertEquals("<!DOCTYPE html><head><title></title></head><body>Custom File 404</body></html>", response.body)
    }

    spec fun when_an_error_occurs_and_corresponding_error_file_does_not_exist_it_should_serve_default_error_file() {

        TestServer.reset()
        TestServer.appServer.serveErrorsFromFolder("/public")

        TestServer.appServer.get("/notvalid", { response.setStatus(StatusCodes.Forbidden)})
        val response = get("http://localhost:3000/notvalid", hashMapOf())

        assertEquals(403, response.statusCode)
        assertEquals("<!DOCTYPE html><head><title></title></head><body>Standard Error File</body></html>", response.body)
    }
}
