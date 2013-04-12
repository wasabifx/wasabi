package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import io.netty.handler.codec.http.HttpMethod


public class Route(val path: String, val method: HttpMethod, val handler: RouteHandler.() -> Unit) {

    public fun matchesPath(path: String): Boolean {
        return this.path == path

    }

}
