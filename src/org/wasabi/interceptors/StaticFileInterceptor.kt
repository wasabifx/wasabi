package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import java.io.File
import org.wasabi.routing.ResourceNotFoundException
import io.netty.handler.codec.http.HttpMethod


public class StaticFileInterceptor(val path: String): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.GET && request.path.startsWith(path)) {
            var fullPath: String;
            if (path.startsWith("/")) {
                fullPath = request.uri.dropWhile { it == '/' }
            } else {
                fullPath = path + "/" + request.uri
            }
            response.streamFile(fullPath)
            return false
        }
        return true
    }

}

fun AppServer.static(path: String) {
    val staticInterceptor = StaticFileInterceptor(path)
    intercept(staticInterceptor, "*", InterceptOn.PreRequest)
}