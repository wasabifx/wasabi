package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response

public class RouteHandler(val request: Request, val response: Response)  {

    var executeNext = false
    var contentNegotiated = false

    public fun next() {
        executeNext = true
    }

    public fun negotiate(negotiation: () -> Unit) {
        response.overrideContentNegotiation = true
        negotiation()
        if (!contentNegotiated) {
            // TODO: throw exception that cannot negotiate content
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
            // if valid set contentNegotiated to true
        }
    }




}

