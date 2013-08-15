package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import java.io.File
import org.wasabi.exceptions.ResourceNotFoundHttpException
import io.netty.handler.codec.http.HttpMethod


public class StaticFileInterceptor(val folder: String): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        if (request.method == HttpMethod.GET && request.path.startsWith(folder)) {
            var fullPath: String;
            if (folder.startsWith("/")) {
                fullPath = request.uri.dropWhile { it == '/' }
            } else {
                fullPath = folder + "/" + request.uri
            }
            response.streamFile(fullPath)
            return false
        }
        return true
    }

}

fun AppServer.serveStaticFilesFromFolder(folder: String) {
    val staticInterceptor = StaticFileInterceptor(folder)
    intercept(staticInterceptor)
}