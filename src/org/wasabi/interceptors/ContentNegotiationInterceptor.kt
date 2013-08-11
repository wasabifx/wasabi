package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.serializers.Serializer
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import org.wasabi.serializers.JsonSerializer
import org.wasabi.serializers.XmlSerializer


public class ContentNegotiationInterceptor(val serializers: List<Serializer>): Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        if (!(response.overrideContentNegotiation) && response.objectToSend != null && !(response.objectToSend is String)) {
            // TODO: Better handling based on weight of requested accept type
            val serializer = serializers.find { it.canSerialize(request.accept.makeString(","))}
            if (serializer != null) {
                response.overrideSendBuffer(serializer.serialize(response.objectToSend!!))
            }
            // TODO: Handle when you cannot serialize
        }
        return true
    }
}


fun AppServer.negotiateContent() {
    val serializers = arrayListOf<Serializer>()
    serializers.add(JsonSerializer())
    serializers.add(XmlSerializer())
    intercept(ContentNegotiationInterceptor(serializers), "*", InterceptOn.PostRequest)
}
