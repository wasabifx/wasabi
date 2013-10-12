package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import java.util.ArrayList
import org.wasabi.http.CORSEntry
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn

public class CORSInterceptor(val settings: ArrayList<CORSEntry>): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        for (setting in settings) {
            if (setting.path == "*" || request.path.matches(setting.path)) {
                response.addExtraHeader("Access-Control-Request-Method", setting.methods)
                response.addExtraHeader("Access-Control-Allow-Origin", setting.origins)
                if (setting.headers != "") {
                    response.addExtraHeader("Access-Control-Allow-Headers", setting.headers)
                }
                if (setting.credentials != "") {
                    response.addExtraHeader("Access-Control-Allow-Credentials", setting.credentials)
                }
                if (setting.preflightMaxAge != "") {
                    response.addExtraHeader("Access-Control-Max-Age", setting.preflightMaxAge)
                }
                return true
            }
        }
        return true
    }
}

fun AppServer.enableCORSGlobally() {
    enableCORS(arrayListOf(CORSEntry()))
}

fun AppServer.enableCORS(settings: ArrayList<CORSEntry>) {
    intercept(CORSInterceptor(settings), "*", InterceptOn.PostRequest)
}