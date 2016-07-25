package org.wasabi.interceptors

import io.netty.handler.codec.http.HttpMethod
import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import java.util.ArrayList
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.InterceptOn
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabi.routing.Route

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
                    val allowedMethods = routes
                            .filter { routeLocator.compareRouteSegments(it, request.path) }
                            .map { it.method }
                            .toTypedArray()

                    response.setAllowedMethods(allowedMethods)
                    response.addRawHeader("Access-Control-Request-Method", allowedMethods.map { it.name() }.joinToString(","))

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