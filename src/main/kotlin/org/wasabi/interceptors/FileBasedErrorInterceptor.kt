package org.wasabi.interceptors

import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import java.io.File
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn

public class FileBasedErrorInterceptor(val folder: String, val fileExtensions: String = "html", val fallbackGenericFile: String = "error.html"): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        val path = folder.trim('/')
        var fileToServe = "${path}/${response.statusCode}.${fileExtensions}"
        val file = File(fileToServe)
        if (!file.exists()) {
            fileToServe = "${path}/error.html"
        }
        response.setFileResponseHeaders(fileToServe)

        return false
    }
}

public fun AppServer.serveErrorsFromFolder(folder: String, fileExtensions: String = "html", fallbackGenericFile: String = "error.html") {
    intercept(FileBasedErrorInterceptor(folder, fileExtensions, fallbackGenericFile), "*", InterceptOn.Error)
}
