package org.wasabifx.wasabi.test

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.configuration.ConfigurationStorage
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class ConfigStorageSpec: Spek({

    given("a configuration file") {
        val configurationStorage = ConfigurationStorage()
        on("loading valid configuration file") {
            val configuration = configurationStorage.loadFromFile("testData${File.separatorChar}production.json")
            it("should set values correctly") {
                assertEquals(configuration.port, 5000)
                assertEquals(configuration.welcomeMessage, "Welcome to Wasabi!")
                assertEquals(configuration.enableLogging, true)
            }
        }
        on("loading non-existent configuration file") {
            val exception = assertFails({ configurationStorage.loadFromFile("non_existing_file") })
            it("should throw exception with message Configuration file does not exist") {
                assertEquals("Configuration file does not exist", exception.message)
            }
        }
        on("loading configuration file with invalid property value") {
            val exception = assertFails({ configurationStorage.loadFromFile("testData${File.separatorChar}production_bad_property.json")})
            it("should throw exception with message containing name of invalid property") {
                assertTrue(exception.message!!.contains("Invalid property in configuration file: Unrecognized field \"invalid_property\" (class org.wasabifx.wasabi.app.AppConfiguration), not marked as ignorable"))
            }
        }
        on("loading configuration file with invalid JSON") {
            val exception = assertFails({ configurationStorage.loadFromFile("testData${File.separatorChar}production_bad_json.json")})
            it("should throw exception with message indicating Invalid JSON in configuration file") {
                assertEquals("Invalid JSON in configuration file: [Source: testData${File.separatorChar}production_bad_json.json; line: 2, column: 6]", exception.message)
            }
        }
        on("saving a configuration") {
            val file = File.createTempFile("configuration", ".json")
            configurationStorage.saveToFile(AppConfiguration(), file.absolutePath)
            it("should save correctly") {
                file.readText()
                assertTrue(file.exists())
            }
        }
    }
})

