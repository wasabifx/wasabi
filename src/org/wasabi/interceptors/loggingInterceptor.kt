package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.slf4j.LoggerFactory
import org.wasabi.routing.RouteHandler
import org.wasabi.app.AppServer

public class LoggingInterceptor: Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        var logger = LoggerFactory.getLogger(javaClass<LoggingInterceptor>())
        logger!!.info("Requesting ${request.uri}")
        return true
    }

}