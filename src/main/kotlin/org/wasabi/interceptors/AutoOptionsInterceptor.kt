package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import io.netty.handler.codec.http.HttpMethod
import java.util.ArrayList
import org.wasabi.routing.Route
import org.wasabi.http.StatusCodes

public class AutoOptionsInterceptor(val routes: ArrayList<Route>): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.OPTIONS) {
            val methods = routes.filter {
                it.path == request.path
            }.map {
                it.method
            }
            response.addExtraHeader("Allow", methods.makeString(","))
            response.setStatus(StatusCodes.OK)
            return false
        }
        return true
    }
}

fun AppServer.enableAutoOptions() {
    intercept(AutoOptionsInterceptor(routes))
}