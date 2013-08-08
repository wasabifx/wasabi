package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import java.io.File
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn

public class FileBasedErrorInterceptor(val folder: String, val fileExtensions: String = "html", val fallbackGenericFile: String = "error.html"): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        val path = sanitizePath(folder)
        var fileToServe = "${path}/${response.statusCode}.${fileExtensions}"
        val file = File(fileToServe)
        if (!file.exists()) {
            fileToServe = "${path}/error.html"
        }
        response.streamFile(fileToServe)
        return false
    }
}

fun AppServer.serveErrorsFromFolder(folder: String, fileExtensions: String = "html", fallbackGenericFile: String = "error.html") {
    intercept(FileBasedErrorInterceptor(folder, fileExtensions, fallbackGenericFile), "*", InterceptOn.Error)
}