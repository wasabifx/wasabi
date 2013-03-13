package com.hadihariri.wasabi.test

import org.junit.Test as test
import com.hadihariri.wasabi.AppConfiguration
import com.hadihariri.wasabi.AppServer
import kotlin.test.assertEquals
import java.net.Socket
import java.net.SocketAddress
import java.net.InetSocketAddress

public class AppServerConfigurationTests {

    test fun creating_an_app_server_without_explicit_configuration_should_use_default_debug_configuration() {

        val appServer = AppServer()


        assertEquals(3000, appServer.configuration.port)
        assertEquals("Welcome to Wasabi!", appServer.configuration.welcomeMessage)
        assertEquals(true, appServer.configuration.enableLogging)


    }

    test fun creating_an_app_server_with_explicit_configuration_should_use_the_configuration_specified() {

        val appServer = AppServer("testData/production.json")

        assertEquals(5000, appServer.configuration.port)
        assertEquals("Welcome to Wasabi!", appServer.configuration.welcomeMessage)
        assertEquals(true, appServer.configuration.enableLogging)

    }



}