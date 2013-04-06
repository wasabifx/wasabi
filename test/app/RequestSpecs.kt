package org.wasabi.test


import org.junit.Test as spec
import org.wasabai.test.TestServer
import org.wasabai.test.get
import org.wasabi.http.HttpMethod
import kotlin.test.assertEquals
import java.util.ArrayList
import org.wasabi.routing.Routes


public class RequestSpecs {

    spec fun request_should_contain_all_fields() {

        var uri = ""
        var port = 0
        var host = ""
        var userAgent = ""
        var keepAlive = false
        var cacheControl = ""
        var accept =  Array<String>(0, {""})
        var acceptEncoding =  Array<String>(0, {""})
        var acceptLanguage =  Array<String>(0, {""})
        var acceptCharset =  Array<String>(0, {""})


        Routes.get("/",
        {


                uri = request.uri
                host = request.host
                port = request.port
                userAgent = request.userAgent
                keepAlive = request.keepAlive
                cacheControl = request.cacheControl
                accept = request.accept
                acceptEncoding = request.acceptEncoding
                acceptLanguage = request.acceptLanguage
                acceptCharset = request.acceptCharset
                response.send("/")

        })
        TestServer.start()

        get("http://localhost:3000")

        assertEquals("/", uri);
        assertEquals("localhost", host);
        assertEquals(3000, port);
        assertEquals("test-client", userAgent);
        assertEquals(true, keepAlive);
        assertEquals("max-age=0", cacheControl);
        assertEquals(3, accept.size);
        assertEquals(3, acceptEncoding.size);
        assertEquals(2, acceptLanguage.size);
        assertEquals(3, acceptCharset.size);

        TestServer.stop()


    }



}

