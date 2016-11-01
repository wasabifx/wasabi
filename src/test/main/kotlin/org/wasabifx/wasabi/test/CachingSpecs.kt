package org.wasabifx.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals

class CachingSpecs: TestServerContext() {

    @spec fun setting_cache_control_should_set_cache_control_header_in_response() {

        TestServer.appServer.get("/cachePolicy",{
            response.cacheControl = "no-cache"
            response.send("no-cache")
        } )

        val response = get("http://localhost:${TestServer.definedPort}/cachePolicy", hashMapOf())

        val cacheControlHeader = response.headers.firstOrNull({ it.name == "Cache-Control" })?.value

        assertEquals("no-cache", cacheControlHeader)




    }

}