package org.wasabi.test



import org.junit.Test as spec
import org.wasabi.test.get
import kotlin.test.assertEquals
import org.wasabi.http.ContentType
import org.wasabi.routing.InterceptOn
import org.wasabi.test.TestServer

public class SendSpecs: TestServerContext() {

    spec fun sending_an_object_should_encode_and_send_based_on_contentType() {

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "keep-alive",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        TestServer.appServer.get("/customer/10", {

            val obj = object {
                val name = "John"
            }

            response.send(obj)

        })

        val response = get("http://localhost:3000/customer/10", headers)

        assertEquals("{\"name\":\"John\"}",response.body)





    }

}