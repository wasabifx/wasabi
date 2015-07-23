package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import java.io.File
import io.netty.handler.codec.http.HttpMethod


public class StaticFileInterceptor(val folder: String): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        var executeNext = false
        if (request.method == HttpMethod.GET) {
            val fullPath = "${folder}${request.uri}"
            val file = File(fullPath)
            if (file.exists() && file.isFile()) {
                response.streamFile(fullPath)
            } else {
                executeNext = true
            }
        } else {
            executeNext = true
        }
        return executeNext
    }
}

public fun AppServer.serveStaticFilesFromFolder(folder: String) {
    val staticInterceptor = StaticFileInterceptor(folder)
    intercept(staticInterceptor)
}