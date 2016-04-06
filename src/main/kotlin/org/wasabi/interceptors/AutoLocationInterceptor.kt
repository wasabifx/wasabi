package org.wasabi.interceptors

import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn


public class AutoLocationInterceptor(): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        if (response.statusCode == StatusCodes.Created.code && response.resourceId != null) {
            response.location = "${request.protocol}://${request.host}:${request.port}${request.path}/${response.resourceId}"
        }

        return true
    }
}

public fun AppServer.enableAutoLocation(path: String = "*") {
    intercept(AutoLocationInterceptor(), path, interceptOn = InterceptOn.PostRequest)
}