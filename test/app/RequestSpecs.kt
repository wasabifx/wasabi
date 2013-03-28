package org.wasabi.test


import org.junit.Test as spec
import org.wasabai.test.TestServer
import org.wasabai.test.Get
import org.wasabi.http.HttpMethod
import kotlin.test.assertEquals
import java.util.ArrayList


public class RequestSpecs {

    spec fun request_should_contain_all_fields() {

     //   var host = ""
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


        TestServer.appServer.routes.get("/",
        {
            req, res ->

                uri = req.uri
                host = req.host
                port = req.port
                userAgent = req.userAgent
                keepAlive = req.keepAlive
                cacheControl = req.cacheControl
                accept = req.accept
                acceptEncoding = req.acceptEncoding
                acceptLanguage = req.acceptLanguage
                acceptCharset = req.acceptCharset
                res.send("/")

        })
        TestServer.start()

        Get("http://localhost:3000")

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

