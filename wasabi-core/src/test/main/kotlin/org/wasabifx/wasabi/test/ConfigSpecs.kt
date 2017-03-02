package org.wasabifx.wasabi.test

import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.app.AppServer
import org.junit.Test as spec
import kotlin.test.assertEquals
import java.net.Socket
import java.net.SocketAddress
import java.net.InetSocketAddress

class ConfigSpecs {

    @spec fun creating_an_app_server_without_explicit_configuration_should_use_default_debug_configuration() {
        val appServer = AppServer()

        assertEquals(3000, appServer.configuration.port)
        assertEquals(null, appServer.configuration.hostname)

        assertEquals("Server starting on port 3000", appServer.configuration.welcomeMessage)
        assertEquals(true, appServer.configuration.enableLogging)
    }

    @spec fun creating_an_app_server_with_explicit_configuration_should_use_the_configuration_specified() {


        val appServer = AppServer(
                AppConfiguration(
                        port = 5000,
                        welcomeMessage = "Hello there!",
                        enableLogging = false))

        assertEquals(5000, appServer.configuration.port)
        assertEquals("Hello there!", appServer.configuration.welcomeMessage)
        assertEquals(false, appServer.configuration.enableLogging)
    }

    @spec fun specifying_hostname_changes_default_welcome_message() {
        val appServer = AppServer(
                AppConfiguration(
                        port = 5000,
                        hostname = "127.0.0.1"))

        assertEquals(5000, appServer.configuration.port)
        assertEquals("127.0.0.1", appServer.configuration.hostname)

        assertEquals("Server starting at 127.0.0.1:5000", appServer.configuration.welcomeMessage)
    }
}
