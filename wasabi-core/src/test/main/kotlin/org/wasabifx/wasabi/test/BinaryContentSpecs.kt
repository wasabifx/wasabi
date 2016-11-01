package org.wasabifx.wasabi.test

import kotlin.test.assertEquals
import org.junit.Test as spec

class BinaryContentSpecs: TestServerContext() {

    @spec fun request_with_get_should_contain_all_fields() {

        TestServer.appServer.get("/binary/thing",
                {
                    response.send(byteArrayOf(1,2,3,4,5,6,7,8), "application/octet-stream")
                })

        val response = get("http://localhost:${TestServer.definedPort}/binary/thing")
        assertEquals(8, response.body?.length)
    }
}