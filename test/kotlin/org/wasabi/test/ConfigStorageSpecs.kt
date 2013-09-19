package org.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import kotlin.test.fails
import java.io.File
import kotlin.test.assertTrue
import org.wasabi.configuration.ConfigurationStorage
import org.wasabi.configuration.InvalidConfigurationException
import org.wasabi.app.AppConfiguration

public class ConfigStorageSpecs {

    spec fun loading_a_valid_configuration_file_should_correctly_load_all_values() {

        val configurationStorage = ConfigurationStorage()


        val configuration = configurationStorage.loadFromFile("testData/production.json")

        assertEquals(configuration.port, 5000)
        assertEquals(configuration.welcomeMessage, "Welcome to Wasabi!")
        assertEquals(configuration.enableLogging, true)

    }

    spec fun loading_a_non_existent_configuration_file_should_throw_invalid_configuration_exception() {

        val configurationStorage = ConfigurationStorage()

        val exception = fails({ configurationStorage.loadFromFile("non_existing_file") })


        assertEquals(javaClass<InvalidConfigurationException>(), exception.javaClass)
    }

    spec fun loading_an_invalid_configuration_with_invalid_property_should_throw_invalid_configuration_exception_with_name_of_invalid_property() {

        val configurationStorage = ConfigurationStorage()

        val exception = fails( { configurationStorage.loadFromFile("testData/production_bad_property.json")})

        assertEquals(javaClass<InvalidConfigurationException>(), exception.javaClass)
        assertEquals("Invalid property in configuration file: invalid_property", exception?.getMessage())
    }

    spec fun loading_an_invalid_configuration_with_invalid_json_should_throw_invalid_configuration_exception() {

        val configurationStorage = ConfigurationStorage()

        val exception = fails( { configurationStorage.loadFromFile("testData${File.separatorChar}production_bad_json.json")})

        assertEquals(javaClass<InvalidConfigurationException>(), exception.javaClass)
        assertEquals("Invalid JSON in configuration file: [Source: testData${File.separatorChar}production_bad_json.json; line: 2, column: 6]", exception?.getMessage())
    }

    spec fun saving_a_configuration_to_file_should_save_it_correctly() {

        val configurationStorage = ConfigurationStorage()


        val file = File.createTempFile("configuration", ".json")
        configurationStorage.saveToFile(AppConfiguration(), file.getAbsolutePath())

        val text = file.readText(defaultCharset)

        assertTrue(file.exists())
        assertEquals("{\"port\":3000,\"welcomeMessage\":\"Server starting on port 3000\",\"enableLogging\":true}", text)

    }
}