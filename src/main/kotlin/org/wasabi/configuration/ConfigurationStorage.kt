package org.wasabi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.wasabi.app.AppConfiguration
import org.wasabi.configuration.InvalidConfigurationException

public class ConfigurationStorage {

    public fun loadProduction(): AppConfiguration {
        return loadFromFile("production.json")
    }

    public fun loadDebug(): AppConfiguration {
        return loadFromFile("debug.json")
    }

    public fun loadFromFile(jsonFilename: String): AppConfiguration {
        val objectMapper = ObjectMapper()
        val jsonFile = File(jsonFilename)
        if (jsonFile.exists()) {
            try {
                val configuration = objectMapper.readValue<AppConfiguration>(jsonFile, AppConfiguration::class.java)
                if (configuration != null) {
                    return configuration
                } else {
                    throw InvalidConfigurationException("Could not read configuration")
                }
            } catch (exception: UnrecognizedPropertyException) {
                throw InvalidConfigurationException("Invalid property in configuration file: " + exception.propertyName)
            } catch (exception: JsonParseException) {
                throw InvalidConfigurationException("Invalid JSON in configuration file: " + exception.location)
            }
        } else {
            throw InvalidConfigurationException("Configuration file does not exist")
        }
    }

    public fun saveToFile(configuration: AppConfiguration, jsonFilename: String) {
        val objectMapper = ObjectMapper()
        objectMapper.writeValue(File(jsonFilename), configuration)
    }

}
