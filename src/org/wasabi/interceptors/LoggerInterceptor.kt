package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.slf4j.LoggerFactory

public class LoggerInterceptor: BeforeRequestInterceptor {
    override fun handle(request: Request, response: Response): Boolean {
        var logger = LoggerFactory.getLogger(javaClass<LoggerInterceptor>())
        logger!!.info("Requesting ${request.uri}")
        return true
    }
}