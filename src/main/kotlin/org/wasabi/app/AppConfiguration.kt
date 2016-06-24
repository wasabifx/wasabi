package org.wasabi.app

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.reflect.memberProperties

var configuration : AppConfiguration? = null;


public data class AppConfiguration(
     var port: Int = 3000,
     var welcomeMessage: String = "Server starting on port $port",
     var enableContentNegotiation: Boolean = true,
     var enableLogging: Boolean = true,
     var enableAutoOptions: Boolean = false,
     var enableCORSGlobally: Boolean = false,
     var sessionLifetime: Int = 600,
     var enableXML11: Boolean = false,
     var maxHttpContentLength: Int = 1048576,
     var sslEnabled: Boolean = false,
     var sslCertificatePath: String = ""
)
{
    private val logger = LoggerFactory.getLogger(AppConfiguration::class.java)
    var sections: Map<Any, Any> = HashMap<Any, Any>()

    init{
        val yaml = Yaml()
        try {
            // Here we are simply attempting to load a config in the current location under the
            // assumption Programmatic configuration wont have such present.
            var configuration = yaml.load(FileInputStream(File("wasabi.yaml"))) as MutableMap<Any, Any>

            @Suppress("UNCHECKED_CAST")
            var wasabiConfiguration = configuration["wasabi"] as Map<Any, Any>
            AppConfiguration::class.memberProperties.forEach {
                // Ignore the logger ....
                if (it.name != "logger")
                {
                    try {
                        javaClass.getDeclaredField(it.name).set(this, wasabiConfiguration[it.name]);
                    }
                    catch(exception: Exception)
                    {
                        logger!!.warn("${it.name} setting not found in config, using default.")
                    }
                }
            }

            // Drop wasabi from read config, its now set on object directly.
            configuration.remove("wasabi")

            // Assign custom config as immutable Map.
            sections = configuration as Map<Any, Any>


        }
        catch(exception: Exception)
        {
            logger!!.warn("Unable to load configuration from file: $exception, using defaults or constructor provided values.")
        }
        finally {
            // Populate our static var, currently one instance is only ever created
            // so get's things going, TODO do better...
            org.wasabi.app.configuration = this
        }
    }
}



