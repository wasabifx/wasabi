package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.StatusCodes
import org.wasabi.http.ContentType


public class RouteHandler(val request: Request, val response: Response)  {

    var executeNext = false

    public fun next() {
        executeNext = true
    }

}



fun routeHandler(f: RouteHandler.()->Unit) = f

fun String.with(handler : Response.() -> Unit) : Pair<String, Response.() -> Unit> = this to handler








