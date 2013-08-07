package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import io.netty.handler.codec.http.HttpMethod
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn


public class FavIconInterceptor(val icon: String): Interceptor {

    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.GET && request.uri.compareToIgnoreCase("/favicon.ico") == 0) {
            var fullPath: String = icon;
            if (icon.startsWith("/")) {
                fullPath = icon.dropWhile { it == '/' }
            }
            response.streamFile(fullPath, "image/x-icon")
            return false
        }
        return true
    }

}

fun AppServer.favicon(icon: String) {
    intercept(FavIconInterceptor(icon))
}