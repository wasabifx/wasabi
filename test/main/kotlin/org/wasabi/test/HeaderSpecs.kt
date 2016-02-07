package org.wasabi.test


import org.junit.Test as spec
import org.wasabi.test.TestServer
import org.wasabi.test.get
import kotlin.test.assertEquals
import java.util.ArrayList
import org.wasabi.test.postForm
import org.apache.http.message.BasicNameValuePair
import org.junit.Ignore
import org.wasabi.http.Cookie
import java.util.HashMap
import java.util.SortedMap


public class HeaderSpecs: TestServerContext() {

    @spec fun request_with_get_should_contain_all_fields() {


        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "close",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/html,application/xhtml+xml;q=0.4,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*"

        )

        var uri = ""
        var path = ""
        var port = 0
        var host = ""
        var userAgent = ""
        var connection = ""
        var cacheControl = ""
        var accept =  sortedMapOf<String, Int>()
        var acceptEncoding =  sortedMapOf<String, Int>()
        var acceptLanguage =  sortedMapOf<String, Int>()
        var acceptCharset =  sortedMapOf<String, Int>()
        var queryParams = HashMap<String, String>()
        var routeParams = HashMap<String, String>()



        TestServer.appServer.get("/customer/:id/:section",
        {


                uri = request.uri
                path = request.path
                host = request.host
                port = request.port
                userAgent = request.userAgent
                connection = request.connection
                cacheControl = request.cacheControl
                accept = request.accept
                acceptEncoding = request.acceptEncoding
                acceptLanguage = request.acceptLanguage
                acceptCharset = request.acceptCharset
                queryParams = request.queryParams
                routeParams = request.routeParams
                response.send("/")


        })

        get("http://localhost:${TestServer.definedPort}/customer/10/valid?param1=value1&param2=value2", headers)

        assertEquals("/customer/10/valid?param1=value1&param2=value2", uri);
        assertEquals("/customer/10/valid", path);
        assertEquals("localhost", host);
        assertEquals(TestServer.definedPort, port);
        assertEquals("test-client", userAgent);
        assertEquals("Close", connection);
        assertEquals("max-age=0", cacheControl);
        assertEquals(3, accept.size);
        assertEquals(4, accept["application/xhtml+xml"])
        assertEquals(3, acceptEncoding.size);
        assertEquals(2, acceptLanguage.size);
        assertEquals(3, acceptCharset.size);
        assertEquals(2, queryParams.size)
        assertEquals("value1",queryParams["param1"])
        assertEquals("value2",queryParams["param2"])
        assertEquals("10", routeParams["id"])
        assertEquals("valid", routeParams["section"])


    }

    @spec fun request_with_url_form_encoded_post_should_contain_post_fields_in_bodyParams() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*"

        )

        var bodyParams = HashMap<String, Any>()




        TestServer.appServer.post("/customer",
                {

                    System.out.println(request.bodyParams)

                    bodyParams = request.bodyParams
                    response.send("/")

                })

        val fields = arrayListOf<BasicNameValuePair>(BasicNameValuePair("name", "joe"), BasicNameValuePair("email", "joe@joe.com"))
        postForm("http://localhost:${TestServer.definedPort}/customer", headers, fields)

        assertEquals(2, bodyParams.size)
        assertEquals("joe", bodyParams["name"])
        assertEquals("joe@joe.com", bodyParams["email"])


    }


    @spec fun setting_a_cookie_when_making_a_request_should_set_the_cookie_value_in_the_request() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*"

        )

        var cookies = HashMap<String, Cookie>()

        TestServer.appServer.get("/cookie", {

            cookies = request.cookies
            response.send("Nothing")

        })
        get("http://localhost:${TestServer.definedPort}/cookie", headers)

        assertEquals("someCookieValue", cookies["someCookie"]?.value)




    }


    @spec fun request_with_url_form_encoded_post_and_chunked_encoding_should_contain_post_fields_in_bodyParams() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/html,application/xhtml+xml,application/xml",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        var bodyParams = HashMap<String, Any>()

        TestServer.appServer.post("/customer",
                {
                    bodyParams = request.bodyParams
                    response.send("/")
                })

        val fields = arrayListOf(BasicNameValuePair("name", "joe"), BasicNameValuePair("email", "joe@joe.com"))
        postForm("http://localhost:${TestServer.definedPort}/customer", headers, fields, true)

        assertEquals(2, bodyParams.size)
        assertEquals("joe", bodyParams["name"])
        assertEquals("joe@joe.com", bodyParams["email"])
    }
}

