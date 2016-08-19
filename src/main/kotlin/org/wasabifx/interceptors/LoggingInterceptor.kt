package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.slf4j.LoggerFactory
import org.wasabifx.routing.RouteHandler
import org.wasabifx.app.AppServer
import org.wasabifx.routing.InterceptOn

public class LoggingInterceptor: Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        var logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)
        logger!!.info("[${request.method.toString()}] - ${request.uri}")
        return true
    }

}

public fun AppServer.logRequests() {
    intercept(LoggingInterceptor(), "*", InterceptOn.PreExecution)
}