package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response

public class RouteHandler(val request: Request, val response: Response)  {

    var executeNext = false

    fun next() {
        executeNext = true
    }


}

