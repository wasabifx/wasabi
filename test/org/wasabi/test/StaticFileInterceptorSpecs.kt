package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.serveStaticFilesFromFolder
import kotlin.test.assertEquals

public class StaticFileInterceptorSpecs: TestServerContext() {

    spec fun requesting_an_existing_static_file_when_using_static_file_interceptor_should_return_the_file() {


        TestServer.reset()
        TestServer.appServer.serveStaticFilesFromFolder("/public")

        val response = get("http://localhost:3000/public/test.html", hashMapOf())


        assertEquals("<!DOCTYPE html><head><title></title></head><body>This is an example static file</body></html>", response.body)




    }

    spec fun requesting_an_non_existing_static_file_when_using_static_file_interceptor_should_404() {


        TestServer.reset()
        TestServer.appServer.serveStaticFilesFromFolder("/public")

        val response = get("http://localhost:3000/public/test1.html", hashMapOf())


        assertEquals("Not found", response.body)




    }

}