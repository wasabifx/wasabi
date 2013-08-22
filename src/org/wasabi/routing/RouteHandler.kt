package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpStatusCodes

public class RouteHandler(val request: Request, val response: Response)  {

    var executeNext = false

    public fun next() {
        executeNext = true
    }

}

