package org.wasabi.interceptors

import org.wasabi.interceptors.Interceptor
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import org.wasabi

public class ETagInterceptor(private val objectTagFunc: (Any) -> String = { (obj) -> obj.hashCode().toString() }): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        if (response.sendBuffer != null) {
            val objectTag = objectTagFunc(response.sendBuffer!!)
            val incomingETag = request.ifNoneMatch
            if (incomingETag.compareToIgnoreCase(objectTag) == 0) {
                response.setStatus(304, "Not modified")
                return false
            } else {
                response.etag = objectTag
                return true
            }
        }
        return true
    }
}

fun AppServer.enableETag(path: String = "*", objectTagFunc: (Any) -> String = { obj -> obj.hashCode().toString() }) {
    intercept(ETagInterceptor(objectTagFunc), path, interceptOn = InterceptOn.PostExecution)
}