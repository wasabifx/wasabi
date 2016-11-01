package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.serializers.Serializer
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.StatusCodes


class ContentNegotiationInterceptor(val serializers: List<Serializer>): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        var executeNext = false
            if (response.negotiatedMediaType == "" && (response.sendBuffer != null) && !(response.sendBuffer is String)) {
                for (requestedContentType in response.requestedContentTypes) {
                    val serializer = serializers.firstOrNull { it.canSerialize(requestedContentType) }
                    if (serializer != null) {
                        response.negotiatedMediaType = requestedContentType
                        executeNext = true
                        break
                    }
                }
                if (response.negotiatedMediaType == "") {
                    response.setStatus(StatusCodes.UnsupportedMediaType)
                }
            } else {
                executeNext = true
            }

        return executeNext
    }
}

fun AppServer.enableContentNegotiation() {
    intercept(ContentNegotiationInterceptor(serializers), "*", InterceptOn.PostExecution)
}
