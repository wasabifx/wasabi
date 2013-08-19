package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpStatusCodes

public class RouteHandler(val request: Request, val response: Response)  {

    var executeNext = false
    var contentNegotiated = false

    public fun next() {
        executeNext = true
    }

    // I'm not too happy with this solution because while it does provide
    // a nice DSL for negotiation, technically it does not belong in RouteHandler.
    // It would be part of response and as the content negotiation is an interceptor
    // it should be completely self-contained...but what the hell. (H.H.)

    public fun negotiate(negotiation: () -> Unit) {
        response.overrideContentNegotiation = true
        negotiation()
        if (!contentNegotiated) {
            response.setHttpStatus(HttpStatusCodes.UnsupportedMediaType)
        }
    }

    public fun on(contentType: String, func: () -> Unit) {
        // TODO: Fill these in
        // find content-type
        // if valid, execute
        if (!contentNegotiated) {
            if (request.accept.makeString(",").contains(contentType)) {
                func()
                contentNegotiated = true
            }
        }
    }




}

