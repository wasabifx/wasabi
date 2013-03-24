package com.hadihariri.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import java.net.Socket
import java.net.SocketAddress
import java.net.InetSocketAddress
import com.hadihariri.wasabi.routing.Routes
import com.hadihariri.wasabi.app.AppServer

public class ConfigSpecs {

    spec fun creating_an_app_server_without_explicit_configuration_should_use_default_debug_configuration() {



        val appServer = AppServer()


        assertEquals(3000, appServer.configuration.port)
        assertEquals("Welcome to Wasabi!", appServer.configuration.welcomeMessage)
        assertEquals(true, appServer.configuration.enableLogging)

        appServer.stop()
    }

    spec fun creating_an_app_server_with_explicit_configuration_should_use_the_configuration_specified() {

        val appServer = AppServer("testData/production.json")

        assertEquals(5000, appServer.configuration.port)
        assertEquals("Welcome to Wasabi!", appServer.configuration.welcomeMessage)
        assertEquals(true, appServer.configuration.enableLogging)

        appServer.stop()
    }



}