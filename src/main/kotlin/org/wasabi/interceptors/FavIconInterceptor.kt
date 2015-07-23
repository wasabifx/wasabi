package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import io.netty.handler.codec.http.HttpMethod
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn


public class FavIconInterceptor(val icon: String): Interceptor() {

    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.GET && request.uri.compareTo("/favicon.ico", ignoreCase = true) == 0) {
            val path = sanitizePath(icon)
            response.streamFile(path, "image/x-icon")
            return false
        } else {
            return true
        }
    }

}

fun sanitizePath(path: String): String {
    var sanitizedPath = path.removeSuffix("/")
    if (path.startsWith("/")) {
        sanitizedPath = path.dropWhile { it == '/' }
    }
    return sanitizedPath
}

fun AppServer.serveFavIconAs(icon: String) {
    intercept(FavIconInterceptor(icon))
}