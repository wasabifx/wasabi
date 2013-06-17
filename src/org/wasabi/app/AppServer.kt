package org.wasabi.app

import org.wasabi.routing.Routes
import org.wasabi.http.HttpServer
import org.wasabi.configuration.ConfigurationStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.wasabi.routing.InterceptorOccurence
import java.util.ArrayList
import org.wasabi.interceptors.BeforeRequestInterceptor
import org.wasabi.interceptors.AfterRequestInterceptor
import org.wasabi.interceptors.LoggerInterceptor
import org.wasabi.interceptors.BeforeResponseInterceptor


public class AppServer(val configuration: AppConfiguration = AppConfiguration()) {

    public val beforeRequestHandlers: ArrayList<BeforeRequestInterceptor> = arrayListOf<BeforeRequestInterceptor>()
    public val afterRequestHandlers: ArrayList<AfterRequestInterceptor> = ArrayList<AfterRequestInterceptor>()
    public val beforeResponseInterceptors: ArrayList<BeforeResponseInterceptor> = ArrayList<BeforeResponseInterceptor>()

    private var logger = LoggerFactory.getLogger(javaClass<AppServer>())
    private val httpServer: HttpServer
    private var running = false

    {
        httpServer = HttpServer(this)
        if (configuration.enableLogging) {
            beforeRequestHandlers.add(0, LoggerInterceptor())
        }
    }

    public val isRunning : Boolean
        get ()
            {return running}

    public fun start(wait: Boolean = true) {
        logger!!.info(configuration.welcomeMessage)

        running = true
        httpServer.start(wait)

    }

    public fun stop() {
        httpServer.stop()
        logger!!.info("Server Stopped")
        running = false
    }




}

