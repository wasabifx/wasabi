package org.wasabi.app

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import kotlin.reflect.memberProperties

var configuration : AppConfiguration = null!!;

public data class AppConfiguration(
     var port: Int = 3000,
     var welcomeMessage: String = "Server starting on port $port",
     var enableContentNegotiation: Boolean = true,
     var enableLogging: Boolean = true,
     var enableAutoOptions: Boolean = false,
     var enableCORSGlobally: Boolean = false,
     var sessionLifetime: Int = 600,
     var enableXML11: Boolean = false

)
{
    private val logger = LoggerFactory.getLogger(AppConfiguration::class.java)
    init{
        var yaml = Yaml()
        try {
            // Here we are simply attempting to load a config in the current location under the
            // assumption Programmatic configuration wont have such present.
            var configuration = yaml.load(FileInputStream(File("wasabi.yaml"))) as Map<String, Object>
            var wasabiConfiguration = configuration["wasabi"] as Map<String, Object>
            AppConfiguration::class.memberProperties.forEach {
                if (it.name != "logger")
                {
                    javaClass.getDeclaredField(it.name).set(this, wasabiConfiguration[it.name]);
                }
            }

            // Populate our static var, currently one instance is only ever created
            // so get's things going, TODO do better...
            org.wasabi.app.configuration = this
        }
        catch(exception: Exception)
        {
            logger!!.warn("Unable to load configuration from file: $exception, setting defaults or using constructor provided values.")
        }
    }

}



