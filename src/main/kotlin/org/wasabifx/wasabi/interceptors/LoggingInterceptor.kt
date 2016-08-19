package org.wasabifx.wasabi.interceptors

import org.slf4j.LoggerFactory
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response

class LoggingInterceptor: Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        var logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)
        logger!!.info("[${request.method.toString()}] - ${request.uri}")
        return true
    }

}

fun AppServer.logRequests() {
    intercept(LoggingInterceptor(), "*", InterceptOn.PreExecution)
}