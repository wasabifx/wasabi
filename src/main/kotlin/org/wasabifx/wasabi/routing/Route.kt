package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod
import java.util.*


class Route(val path: String, val method: HttpMethod, val params: HashMap<String, String>, vararg val handler: RouteHandler.() -> Unit) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Route

        if (path != other.path) return false
        if (method != other.method) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + method.hashCode()
        return result
    }
}

