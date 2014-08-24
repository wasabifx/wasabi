package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.serializers.Serializer
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import org.wasabi.serializers.JsonSerializer
import org.wasabi.serializers.XmlSerializer
import org.wasabi.http.StatusCodes


public class ContentNegotiationInterceptor(val serializers: List<Serializer>): Interceptor() {
    override fun intercept(request: Request, response: Response) {
            if (response.negotiatedMediaType == "" && (response.sendBuffer != null) && !(response.sendBuffer is String)) {
                for (requestedContentType in response.requestedContentTypes) {
                    val serializer = serializers.firstOrNull { it.canSerialize(requestedContentType) }
                    if (serializer != null) {
                        response.negotiatedMediaType = requestedContentType
                        next()
                        break;
                    }
                }
                if (response.negotiatedMediaType == "") {
                    response.setStatus(StatusCodes.UnsupportedMediaType)
                }
            } else {
                next()
            }
    }
}







public fun AppServer.enableContentNegotiation() {
    intercept(ContentNegotiationInterceptor(serializers), "*", InterceptOn.PostExecution)
}
