package org.wasabifx.wasabi.test

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.app.AppServer
import kotlin.test.assertEquals
import org.junit.Test as spec


class ConfigSpec: Spek({

    given("an app server") {
        on("creating instance without explicit configuration") {
            val appServer = AppServer()
            it("should use default debug configuration") {
                assertEquals(3000, appServer.configuration.port)
                assertEquals("Server starting on port 3000", appServer.configuration.welcomeMessage)
                assertEquals(true, appServer.configuration.enableLogging)
            }
        }
        on("creating instance with specific configuration") {
            val appServer = AppServer(
                    AppConfiguration(
                            port = 5000,
                            welcomeMessage = "Hello there!",
                            enableLogging = false))
            it("should use provided configuration") {
                assertEquals(5000, appServer.configuration.port)
                assertEquals("Hello there!", appServer.configuration.welcomeMessage)
                assertEquals(false, appServer.configuration.enableLogging)
            }

        }
    }

})

