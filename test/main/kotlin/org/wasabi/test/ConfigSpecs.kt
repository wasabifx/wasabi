package org.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import java.net.Socket
import java.net.SocketAddress
import java.net.InetSocketAddress
import org.wasabi.app.AppServer
import org.wasabi.app.AppConfiguration
import org.wasabi.app.configuration

public class ConfigSpecs {

    spec fun creating_an_app_server_without_explicit_configuration_should_use_default_debug_configuration() {
        val appServer = AppServer()

        assertEquals(3000, configuration.port)
        assertEquals("Server starting on port 3000", configuration.welcomeMessage)
        assertEquals(true, configuration.enableLogging)
    }

    spec fun creating_an_app_server_with_explicit_configuration_should_use_the_configuration_specified() {


        val appServer = AppServer(
                AppConfiguration(
                        port = 5000,
                        welcomeMessage = "Hello there!",
                        enableLogging = false))

        assertEquals(5000, configuration.port)
        assertEquals("Hello there!", configuration.welcomeMessage)
        assertEquals(false, configuration.enableLogging)
    }



}