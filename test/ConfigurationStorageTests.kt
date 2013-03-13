package com.hadihariri.wasabi.test

import org.junit.Test as test
import com.hadihariri.wasabi.AppConfiguration
import kotlin.test.assertEquals
import com.hadihariri.wasabi.ConfigurationStorage
import kotlin.test.fails
import com.hadihariri.wasabi.InvalidConfigurationException
import com.sun.tools.javac.code.TargetType
import java.io.File
import kotlin.test.assertTrue

public class ConfigurationStorageTests {

    test fun loading_a_valid_configuration_file_should_correctly_load_all_values() {

        val configurationStorage = ConfigurationStorage()


        val configuration = configurationStorage.loadFromFile("testData/production.json")

        assertEquals(configuration.port, 5000)
        assertEquals(configuration.welcomeMessage, "Welcome to Wasabi!")
        assertEquals(configuration.enableLogging, true)

    }

    test fun loading_a_non_existent_configuration_file_should_throw_invalid_configuration_exception() {

        val configurationStorage = ConfigurationStorage()

        val exception = fails({ configurationStorage.loadFromFile("non_existing_file") })


        assertEquals(javaClass<InvalidConfigurationException>(), exception.javaClass)
    }

    test fun loading_an_invalid_configuration_with_invalid_property_should_throw_invalid_configuration_exception_with_name_of_invalid_property() {

        val configurationStorage = ConfigurationStorage()

        val exception = fails( { configurationStorage.loadFromFile("testData/production_bad_property.json")})

        assertEquals(javaClass<InvalidConfigurationException>(), exception.javaClass)
        assertEquals("Invalid property in configuration file: invalid_property", exception?.getMessage())
    }

    test fun loading_an_invalid_configuration_with_invalid_json_should_throw_invalid_configuration_exception() {

        val configurationStorage = ConfigurationStorage()

        val exception = fails( { configurationStorage.loadFromFile("testData/production_bad_json.json")})

        assertEquals(javaClass<InvalidConfigurationException>(), exception.javaClass)
        assertEquals("Invalid JSON in configuration file: [Source: testData/production_bad_json.json; line: 2, column: 6]", exception?.getMessage())
    }

    test fun saving_a_configuration_to_file_should_save_it_correctly() {

        val configurationStorage = ConfigurationStorage()

        val configuration = AppConfiguration()

        val file = File.createTempFile("configuration", ".json")
        configurationStorage.saveToFile(configuration, file.getAbsolutePath())

        val text = file.readText(defaultCharset)

        assertTrue(file.exists())
        assertEquals("{\"port\":3000,\"welcomeMessage\":\"Welcome to Wasabi!\",\"enableLogging\":true}", text)

    }
}