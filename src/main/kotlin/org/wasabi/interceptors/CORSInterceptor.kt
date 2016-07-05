package org.wasabi.interceptors

import io.netty.handler.codec.http.HttpMethod
import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import java.util.ArrayList
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.app.AppServer
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.InterceptOn
import org.wasabi.routing.Route

public class CORSInterceptor(val routes: ArrayList<Route>, val settings: ArrayList<CORSEntry>): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.OPTIONS) {
            val methods = routes.filter {
                it.path == request.path
            }.map {
                it.method
            }
            response.addRawHeader("Allow", methods.joinToString(", "))
            for (setting in settings) {
                if (setting.path == "*" || request.path.matches(setting.path.toRegex())) {
                    response.addRawHeader("Access-Control-Request-Method", setting.methods)
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
                }
            }
            response.setStatus(StatusCodes.OK)
        }

        return true
    }
}

public fun AppServer.enableCORSGlobally() {
    enableCORS(arrayListOf(CORSEntry()))
}

public fun AppServer.enableCORS(settings: ArrayList<CORSEntry>) {
    intercept(CORSInterceptor(routes, settings), "*", InterceptOn.PostRequest)
}

public fun AppServer.disableCORS() {
    this.interceptors.removeAll { it.interceptor is CORSInterceptor }
}