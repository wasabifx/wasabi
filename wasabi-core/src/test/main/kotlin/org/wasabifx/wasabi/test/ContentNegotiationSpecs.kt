package org.wasabifx.wasabi.test

import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.with
import java.lang.NullPointerException
import kotlin.test.assertEquals
import org.junit.Test as spec


class ContentNegotiationSpecs : TestServerContext() {

    @spec fun requesting_charset_should_respond_with_such() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "utf-8"
        )

        val utf8Test = "Adélaïde"

        TestServer.appServer.get("/customer/9", {

            val obj = object {
                val name = utf8Test
                val email = utf8Test + "@foo.com"

            }

            response.send(obj)

        })

        val response = get("http://localhost:${TestServer.definedPort}/customer/9", headers)

        assertEquals(StatusCodes.OK.code,response.statusCode)
        assertEquals("{\"name\":\"Adélaïde\",\"email\":\"Adélaïde@foo.com\"}",response.body)

    }

    @spec fun sending_an_object_should_encode_and_send_based_on_contentType() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"
        )

        TestServer.appServer.get("/customer/10", {

            val obj = object {
                val name = "Joe"
                val email = "Joe@smith.com"

            }

            response.send(obj)

        })

        val response = get("http://localhost:${TestServer.definedPort}/customer/10", headers)

        assertEquals(StatusCodes.OK.code,response.statusCode)
        assertEquals("{\"name\":\"Joe\",\"email\":\"Joe@smith.com\"}",response.body)





    }

    @spec fun manual_negotiation_should_execute_correct_body_structure_and_serialize_if_necessary() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        TestServer.appServer.get("/customer/10", {


            val obj = object {
                val name = "Joe"
                val email = "Joe@smith.com"

            }

            response.negotiate(
                    "text/html".with { send ("this is not the response you're looking for")},
                    "application/json" .with { send(obj) }
            )

        })

        val response = get("http://localhost:${TestServer.definedPort}/customer/10", headers)



        assertEquals(StatusCodes.OK.code,response.statusCode)
        assertEquals("{\"name\":\"Joe\",\"email\":\"Joe@smith.com\"}",response.body)





    }


    @spec fun sending_content_type_when_using_send_should_serialize_using_the_requested_content_type() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        TestServer.appServer.get("/customer/10", {


            val obj = object {
                val name = "Joe"
                val email = "Joe@smith.com"

            }

            response.send(obj, "application/json")

        })

        val response = get("http://localhost:${TestServer.definedPort}/customer/10", headers)

        assertEquals(StatusCodes.OK.code,response.statusCode)
        assertEquals("{\"name\":\"Joe\",\"email\":\"Joe@smith.com\"}",response.body)

    }

    @spec fun should_apply_after_exception_appears() {
        TestServer.appServer.exception(NullPointerException::class, {
            response.send(exception)
        })
        TestServer.appServer.get("/throwException", { throw NullPointerException("Something went wrong") })
        val expectedContentType = "application/json"
        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Cache-Control" to "max-age=0",
                "Accept" to expectedContentType,
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"
        )

        val response = get("http://localhost:${TestServer.definedPort}/throwException", headers)

        val actualContentType = response.headers.filter { it.name == "Content-Type" }
                .first().value.split(";").first()
        assertEquals(expectedContentType, actualContentType)
        assertEquals("{\"cause\":null", response.body?.substring(0, 13))
    }

    @spec fun returning_content_as_string_with_custom_content_type_must_work() {
        TestServer.appServer.get("/json", { response.send("{}", "application/json") })
        TestServer.appServer.get("/xml", { response.send("<xml></xml>", "text/xml") })

        val response = get("http://localhost:${TestServer.definedPort}/json")
        assertEquals("application/json", getHeader("Content-Type", response))

        val response2 = get("http://localhost:${TestServer.definedPort}/xml")
        assertEquals("text/xml", getHeader("Content-Type", response2))
    }

    @spec fun setting_explicit_content_type_should_work() {
        TestServer.appServer.get("/test", {
            response.send("TEST".toByteArray(Charsets.UTF_8), "application/octet-stream" )
        })

        val response = get("http://localhost:${TestServer.definedPort}/test")
        assertEquals("application/octet-stream", getHeader("Content-Type", response))
    }
}