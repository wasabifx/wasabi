package org.wasabi.routing

import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response


public class RouteHandler(public val request: Request, public val response: Response)  {

    var executeNext = false


    public fun next() {
        executeNext = true
    }

}

public fun routeHandler(f: RouteHandler.()->Unit): RouteHandler.() -> Unit = f

public fun String.with(handler : Response.() -> Unit) : Pair<String, Response.() -> Unit> = this to handler








