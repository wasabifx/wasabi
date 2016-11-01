package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.app.AppServer
import io.netty.handler.codec.http.HttpMethod
import java.util.ArrayList
import org.wasabifx.wasabi.routing.Route
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.PatternAndVerbMatchingRouteLocator

class AutoOptionsInterceptor(val routes: ArrayList<Route>): Interceptor {
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
