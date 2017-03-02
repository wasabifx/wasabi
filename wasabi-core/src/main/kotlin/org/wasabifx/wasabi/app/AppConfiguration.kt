package org.wasabifx.wasabi.app

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.reflect.memberProperties

var configuration : AppConfiguration? = null


data class AppConfiguration(
        var port: Int = 3000,
        var hostname: String? = null,
        var welcomeMessage: String = "Server starting ${hostname?.let { "at $hostname:$port"  } ?: "on port $port"}",
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
    private val logger = org.slf4j.LoggerFactory.getLogger(AppConfiguration::class.java)
    var sections: Map<Any, Any> = HashMap()

    init{
        // Check we have a wasabi configuration, if not assume programmatic configuration.
        val configurationFile = File("wasabi.yaml")
        val exists = configurationFile.exists()
        if(exists) {
            val yaml = Yaml()
            try {
                @Suppress("UNCHECKED_CAST")
                val configuration = yaml.load(FileInputStream(configurationFile)) as MutableMap<Any, Any>

                @Suppress("UNCHECKED_CAST")
                val wasabiConfiguration = configuration["wasabi"] as Map<Any, Any>
                AppConfiguration::class.memberProperties.forEach {
                    // Ignore the logger ....
                    if (it.name != "logger")
                    {
                        try {
                            javaClass.getDeclaredField(it.name).set(this, wasabiConfiguration[it.name])
                        }
                        catch(exception: Exception)
                        {
                            logger!!.debug("${it.name} setting not found in config, using default.")
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
                logger!!.debug("Unable to load configuration from file: $exception, using defaults or constructor provided values.")
            }
        }
        // Populate our static var, currently one instance is only ever created
        // so get's things going.
        configuration = this
    }
}
