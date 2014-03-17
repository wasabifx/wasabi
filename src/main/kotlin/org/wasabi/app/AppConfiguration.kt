package org.wasabi.app

import org.codehaus.jackson.map.ObjectMapper
import java.io.File



public data class AppConfiguration(
     var port: Int = 3000,
     var welcomeMessage: String = "Server starting on port $port",
     var enableContentNegotiation: Boolean = true,
     var enableLogging: Boolean = true,
     var enableAutoOptions: Boolean = false,
     var enableCORSGlobally: Boolean = false,
     var sessionLifetime: Int = 600)

