package org.wasabi.interceptors

import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import java.util.ArrayList
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn

public class CORSInterceptor(val settings: ArrayList<CORSEntry>): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
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
                return true
            }
        }
        return true
    }
}

public fun AppServer.enableCORSGlobally() {
    enableCORS(arrayListOf(CORSEntry()))
}

public fun AppServer.enableCORS(settings: ArrayList<CORSEntry>) {
    intercept(CORSInterceptor(settings), "*", InterceptOn.PostRequest)
}