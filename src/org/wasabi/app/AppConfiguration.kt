package org.wasabi.app

import org.codehaus.jackson.map.ObjectMapper
import java.io.File



public data class AppConfiguration(
     var port: Int = 3000,
     var welcomeMessage: String = "Welcome to Wasabi!",
     var enableLogging: Boolean = true)

