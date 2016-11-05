package org.wasabifx.wasabi.routing

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response


class RouteHandler(val request: Request, val response: Response)  {
    var executeNext = false
    fun next() {
        executeNext = true
    }
}

fun routeHandler(f: RouteHandler.()->Unit): RouteHandler.() -> Unit = f

fun String.with(handler : Response.() -> Unit) : Pair<String, Response.() -> Unit> = this to handler








