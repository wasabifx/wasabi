package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.StatusCodes
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn


public class AutoLocationInterceptor(): Interceptor() {
    override fun intercept(request: Request, response: Response) {
        if (response.statusCode == StatusCodes.Created.code && response.resourceId != null) {
            response.location = "${request.protocol}://${request.host}:${request.port}${request.path}/${response.resourceId}"
        }

        next()
    }
}

fun AppServer.enableAutoLocation(path: String = "*") {
    intercept(AutoLocationInterceptor(), path, interceptOn = InterceptOn.PostRequest)
}