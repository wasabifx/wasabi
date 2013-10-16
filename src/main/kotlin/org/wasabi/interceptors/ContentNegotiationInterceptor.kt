package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.serializers.Serializer
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import org.wasabi.serializers.JsonSerializer
import org.wasabi.serializers.XmlSerializer
import org.wasabi.http.StatusCodes


public class ContentNegotiationInterceptor(val serializers: List<Serializer>): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
            if (response.negotiatedMediaType == "" && (response.sendBuffer != null) && !(response.sendBuffer is String)) {
                for (requestedContentType in response.requestedContentTypes) {
                    val serializer = serializers.find { it.canSerialize(requestedContentType) }
                    if (serializer != null) {
                        response.send(serializer.serialize(response.sendBuffer!!))
                        return true
                    }
                }
                response.setStatus(StatusCodes.UnsupportedMediaType)
                return false
            }
            return true
    }
}







fun AppServer.enableContentNegotiation() {
    intercept(ContentNegotiationInterceptor(serializers), "*", InterceptOn.PostExecution)
}
