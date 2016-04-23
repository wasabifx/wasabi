package org.wasabi.test

import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.with
import kotlin.test.assertEquals
import org.junit.Test as spec


public class ContentNegotiationSpecs : TestServerContext() {

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
}