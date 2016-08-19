package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import io.netty.handler.codec.http.HttpMethod
import org.wasabifx.wasabi.app.AppServer


class FavIconInterceptor(val icon: String): Interceptor() {

    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.GET && request.uri.compareTo("/favicon.ico", ignoreCase = true) == 0) {
            val path = icon.trim('/')
            response.setFileResponseHeaders(path, "image/x-icon")
            return false
        } else {
            return true
        }
    }

}

fun AppServer.serveFavIconAs(icon: String) {
    intercept(FavIconInterceptor(icon))
}
