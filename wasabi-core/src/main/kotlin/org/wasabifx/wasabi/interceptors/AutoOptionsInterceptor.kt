package org.wasabifx.wasabi.interceptors

import io.netty.handler.codec.http.HttpMethod
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabifx.wasabi.routing.Route

class AutoOptionsInterceptor(val routes: Set<Route>): Interceptor {
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
