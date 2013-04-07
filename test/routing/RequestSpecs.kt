package org.wasabi.test


import org.junit.Test as spec
import org.wasabai.test.TestServer
import org.wasabai.test.get
import org.wasabi.http.HttpMethod
import kotlin.test.assertEquals
import java.util.ArrayList
import org.wasabi.routing.Routes
import org.wasabi.routing.QueryParams


public class RequestSpecs {

    spec fun request_should_contain_all_fields() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "keep-alive",
                "Cache-Control" to "max-age=0",
                "Accept" to "Accept=text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

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
        var queryParams = QueryParams()


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
                queryParams = request.queryParams
                response.send("/")

        })
        TestServer.start()

        get("http://localhost:3000?param1=value1&param2=value2", headers)

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
        assertEquals(2, queryParams.size())
        assertEquals("value1",queryParams["param1"])
        assertEquals("value2",queryParams["param2"])
        TestServer.stop()


    }



}

