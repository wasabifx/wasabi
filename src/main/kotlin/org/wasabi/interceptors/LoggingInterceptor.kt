package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.slf4j.LoggerFactory
import org.wasabi.routing.RouteHandler
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn

public class LoggingInterceptor: Interceptor() {
    override fun intercept(request: Request, response: Response) {
        var logger = LoggerFactory.getLogger(javaClass<LoggingInterceptor>())
        logger!!.info("[${request.method.toString()}] - ${request.uri}")
        next()
    }

}

public fun AppServer.logRequests() {
    intercept(LoggingInterceptor(), "*", InterceptOn.PreExecution)
}