package com.hadihariri.wasabi.test

import org.junit.Test as test
import com.hadihariri.wasabi.HttpServer
import kotlin.test.assertEquals
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import kotlin.test.assertNotNull
import com.hadihariri.wasabi.AppConfiguration


public class HttpServerTests {

    test fun ServerStartsAndListensCorrectly() {

        val configuration = AppConfiguration()

        val server = HttpServer(configuration)

        server.start()



     //   assertEquals(welcomeMessage, response?.getStatusLine())
    }
}
