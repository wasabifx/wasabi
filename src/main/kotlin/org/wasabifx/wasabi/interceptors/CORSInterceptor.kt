package org.wasabifx.wasabi.interceptors

import io.netty.handler.codec.http.HttpMethod
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.CORSEntry
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabifx.wasabi.routing.Route
import java.util.*

class CORSInterceptor(val routes: ArrayList<Route>, val settings: ArrayList<CORSEntry>): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        val routeLocator = PatternAndVerbMatchingRouteLocator(routes)

        for (setting in settings) {
            if (setting.path == "*" || request.path.matches(setting.path.toRegex())) {

                // This covers non options requests, browser expects the below on the
                // request subsequent to the options request on CORS transfers.
                if (response.statusCode == StatusCodes.OK.code) {
                    response.addRawHeader("Access-Control-Allow-Origin", setting.path)
                }

                // This handles the initial OPTIONS request during the CORS transfer.
                if (request.method == HttpMethod.OPTIONS) {
                    val availableMethods = routes
                            .filter { routeLocator.compareRouteSegments(it, request.path) }
                            .map { it.method }
                            .toSet()

                    val allowedMethods = if (setting.methods == CORSEntry.ALL_AVAILABLE_METHODS) {
                        availableMethods
                    } else {
                        availableMethods.intersect(setting.methods)
                    }

                    response.addRawHeader("Access-Control-Allow-Methods", allowedMethods.map { it.name() }.joinToString(","))

                    response.addRawHeader("Access-Control-Allow-Origin", setting.origins)
                    if (setting.headers != "") {
                        response.addRawHeader("Access-Control-Allow-Headers", setting.headers)
                    }
                    if (setting.credentials != "") {
                        response.addRawHeader("Access-Control-Allow-Credentials", setting.credentials)
                    }
                    if (setting.preflightMaxAge != "") {
                        response.addRawHeader("Access-Control-Max-Age", setting.preflightMaxAge)
                    }

                    response.setStatus(StatusCodes.OK)
                }
            }
        }

        return true
    }
}

fun AppServer.enableCORSGlobally() {
    enableCORS(arrayListOf(CORSEntry()))
}

fun AppServer.enableCORS(settings: ArrayList<CORSEntry>) {
    intercept(CORSInterceptor(routes, settings), "*", InterceptOn.PostRequest)
}

fun AppServer.disableCORS() {
    this.interceptors.removeAll { it.interceptor is CORSInterceptor }
}