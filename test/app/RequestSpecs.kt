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
        var host = ""
        var userAgent = ""
        var keepAlive = false
        var cacheControl = ""
        var accept =  Array<String>(0, {""})

        TestServer.appServer.routes.addRoute(HttpMethod.GET, "/",
        {
            req, res ->

                uri = req.uri;
                host = req.host;
                userAgent = req.userAgent
                keepAlive = req.keepAlive
                cacheControl = req.cacheControl
                accept = req.accept

                res.send("/")

        })
        TestServer.start()

        Get("http://localhost:3000")

        assertEquals("/", uri);
        assertEquals("localhost", host);
        assertEquals("test-client", userAgent);
        assertEquals(true, keepAlive);
        assertEquals("max-age=0", cacheControl);
        assertEquals(3, accept.size);

        TestServer.stop()


    }



}

