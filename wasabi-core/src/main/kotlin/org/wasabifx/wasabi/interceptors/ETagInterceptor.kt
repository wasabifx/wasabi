package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response

class ETagInterceptor(private val objectTagFunc: (Any) -> String = { obj -> obj.hashCode().toString() }): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        var executeNext = false
        if (response.sendBuffer != null) {
            val objectTag = objectTagFunc(response.sendBuffer!!)
            val incomingETag = request.ifNoneMatch
            if (incomingETag.compareTo(objectTag, ignoreCase = true) == 0) {
                response.setStatus(304, "Not modified")
            } else {
                response.etag = objectTag
                executeNext = true
            }
        } else {
            executeNext = true
        }
        return executeNext
    }
}

fun AppServer.enableETag(path: String = "*", objectTagFunc: (Any) -> String = { obj -> obj.hashCode().toString() }) {
    intercept(ETagInterceptor(objectTagFunc), path, interceptOn = InterceptOn.PostExecution)
}