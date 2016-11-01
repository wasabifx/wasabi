package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.app.AppServer


class AutoLocationInterceptor(): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        if (response.statusCode == StatusCodes.Created.code && response.resourceId != null) {
            response.location = "${request.protocol}://${request.host}:${request.port}${request.path}/${response.resourceId}"
        }

        return true
    }
}

fun AppServer.enableAutoLocation(path: String = "*") {
    intercept(AutoLocationInterceptor(), path, interceptOn = InterceptOn.PostRequest)
}