package org.wasabifx.wasabi.interceptors

import io.netty.handler.codec.http.HttpMethod
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import java.io.File
import java.net.URLDecoder


class StaticFileInterceptor(val folder: String, val useDefaultFile: Boolean = false, val defaultFile: String = "index.html") : Interceptor {

    private val absoluteFolder: String = File(folder).canonicalPath.toString()

    override fun intercept(request: Request, response: Response): Boolean {
        var executeNext = false

        if (request.method == HttpMethod.GET) {

            val uriPath = URLDecoder.decode(
                if( request.uri.contains("?") ) request.uri.substringBefore("?") else request.uri,
                Charsets.UTF_8.toString()
            )

            val fullPath = "${absoluteFolder}${uriPath}"
            val file = File(fullPath)

            if (!file.canonicalPath.startsWith(absoluteFolder)) {
                throw RuntimeException("Attempt to open file outside of static file folder")
            }

            when {
                file.exists() && file.isFile -> response.sendFile(fullPath)
                file.exists() && file.isDirectory && useDefaultFile -> response.sendFile("${fullPath}/${defaultFile}")
                else -> executeNext = true
            }
        } else {
            executeNext = true
        }
        return executeNext
    }
}

fun AppServer.serveStaticFilesFromFolder(folder: String, useDefaultFile: Boolean = false, defaultFile: String = "index.html") {
    val staticInterceptor = StaticFileInterceptor(folder, useDefaultFile, defaultFile)
    intercept(staticInterceptor)
}