package org.wasabi.test


import org.junit.Test as spec
import org.wasabai.test.TestServer
import org.wasabai.test.get
import kotlin.test.assertEquals
import java.util.ArrayList
import org.wasabi.routing.Routes
import org.wasabi.routing.BaseParams
import org.wasabi.routing.RouteParams
import org.wasabi.routing.QueryParams
import org.wasabi.http.BodyParams
import org.wasabai.test.postForm
import org.apache.http.message.BasicNameValuePair


public class RequestSpecs {

    spec fun request_with_get_should_contain_all_fields() {

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
        var routeParams = RouteParams()



        Routes.get("/customer/:id/:section",
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
                routeParams = request.routeParams
                response.send("/")

        })
        TestServer.start()

        get("http://localhost:3000/customer/10/valid?param1=value1&param2=value2", headers)

        assertEquals("/customer/10/valid", uri);
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
        assertEquals("10", routeParams["id"])
        assertEquals("valid", routeParams["section"])
        TestServer.stop()


    }

    spec fun request_with_url_form_encoded_post_should_contain_post_fields_in_bodyParams() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "keep-alive",
                "Cache-Control" to "max-age=0",
                "Accept" to "Accept=text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        var bodyParams = BodyParams()




        Routes.post("/customer/",
                {


                    bodyParams = request.bodyParams
                    response.send("/")

                })
        TestServer.start()

        val fields = arrayListOf<BasicNameValuePair>(BasicNameValuePair("name", "joe"), BasicNameValuePair("email", "joe@joe.com"))
        postForm("http://localhost:3000/customer", headers, fields)

        assertEquals(2, bodyParams.size())
        assertEquals("joe", bodyParams["name"])
        assertEquals("joe@joe.com", bodyParams["email"])
        TestServer.stop()


    }


}

