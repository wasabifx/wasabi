package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.wasabifx.protocol.http.StatusCodes
import org.wasabifx.app.AppServer
import org.wasabifx.routing.InterceptOn


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