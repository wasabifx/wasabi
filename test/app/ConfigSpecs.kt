package org.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import java.net.Socket
import java.net.SocketAddress
import java.net.InetSocketAddress
import org.wasabi.routing.Routes
import org.wasabi.app.AppServer
import org.wasabi.app.AppConfiguration

public class ConfigSpecs {

    spec fun creating_an_app_server_without_explicit_configuration_should_use_default_debug_configuration() {
        val appServer = AppServer()

        assertEquals(3000, appServer.configuration.port)
        assertEquals("Welcome to Wasabi!", appServer.configuration.welcomeMessage)
        assertEquals(true, appServer.configuration.enableLogging)
    }

    spec fun creating_an_app_server_with_explicit_configuration_should_use_the_configuration_specified() {

        val appServer = AppServer(
                object: AppConfiguration() {
                    {
                        port = 5000
                        welcomeMessage = "Hello there!"
                        enableLogging = false
                    }
        })

        assertEquals(5000, appServer.configuration.port)
        assertEquals("Hello there!", appServer.configuration.welcomeMessage)
        assertEquals(false, appServer.configuration.enableLogging)
    }



}