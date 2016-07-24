package org.wasabi.interceptors

import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import org.wasabi.app.AppServer
import io.netty.handler.codec.http.HttpMethod
import java.util.ArrayList
import org.wasabi.routing.Route
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator

class AutoOptionsInterceptor(val routes: ArrayList<Route>): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        var executeNext = false
        if (request.method == HttpMethod.OPTIONS) {
            val routeLocator = PatternAndVerbMatchingRouteLocator(routes)

            val allowedMethods = routes
                .filter { routeLocator.compareRouteSegments(it, request.path) }
                .map { it.method }
                .toTypedArray()

            response.setAllowedMethods(allowedMethods)
            response.setStatus(StatusCodes.OK)
        } else {
            executeNext = true
        }

        return executeNext
    }
}

fun AppServer.enableAutoOptions() {
    intercept(AutoOptionsInterceptor(routes))
}
fun AppServer.disableAutoOptions() {
    this.interceptors.removeAll { it.interceptor is AutoOptionsInterceptor }
}
