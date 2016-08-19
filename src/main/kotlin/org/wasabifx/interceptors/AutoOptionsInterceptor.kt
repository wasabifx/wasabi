package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.wasabifx.app.AppServer
import io.netty.handler.codec.http.HttpMethod
import java.util.ArrayList
import org.wasabifx.routing.Route
import org.wasabifx.protocol.http.StatusCodes
import org.wasabifx.routing.PatternAndVerbMatchingRouteLocator

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
