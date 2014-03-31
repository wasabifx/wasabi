package org.wasabi.test

import org.junit.Test as spec
import java.util.ArrayList
import kotlin.test.assertEquals
import org.wasabi.interceptors.enableContentNegotiation
import org.wasabi.interceptors.parseContentNegotiationHeaders

public class ContentNegotiatioParserInterceptorSpecs: TestServerContext() {

    spec fun acceptHeader_should_parse_the_accept_header_into_requested_content_types_with_each_media_type_on_own_line() {

        TestServer.appServer.parseContentNegotiationHeaders {
            onAcceptHeader()
        }

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "text/plain;q=0.8,application/xml,application/xhtml+xml,text/html;q=0.9",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*"

        )

        var sanitizedRequestedContentTypes : ArrayList<String> = arrayListOf()


        TestServer.appServer.get("/contentTypes",
                {


                    sanitizedRequestedContentTypes = response.requestedContentTypes
                    response.send("/")

                })

        get("http://localhost:${TestServer.definedPort}/contentTypes", headers)

        assertEquals("application/xhtml+xml", sanitizedRequestedContentTypes.get(0))
        assertEquals("application/xml", sanitizedRequestedContentTypes.get(1))
        assertEquals("text/html", sanitizedRequestedContentTypes.get(2))
        assertEquals("text/plain", sanitizedRequestedContentTypes.get(3))




    }

    spec fun format_should_parse_the_url_query_into_requested_content_type() {

        TestServer.appServer.parseContentNegotiationHeaders {
            onQueryParameter()
            onAcceptHeader()
            onExtension()
        }

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*"

        )

        var sanitizedRequestedContentTypes : ArrayList<String> = arrayListOf()


        TestServer.appServer.get("/contentTypes",
                {


                    sanitizedRequestedContentTypes = response.requestedContentTypes
                    response.send("/")

                })

        get("http://localhost:${TestServer.definedPort}/contentTypes?format=json", headers)

        assertEquals("application/json", sanitizedRequestedContentTypes.get(0))




    }

}
    