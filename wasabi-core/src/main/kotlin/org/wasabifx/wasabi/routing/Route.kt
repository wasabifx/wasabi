package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod


class Route(val path: String, val method: HttpMethod, vararg val handler: RouteHandler.() -> Unit) {
    val segments: List<String> by lazy {
        path.split('/')
    }

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

    fun compareSegmentsToPath(path: String): Boolean {
        val segments2 = path.split('/')
        if (segments.size != segments2.size) {
            return false
        }
        var i = 0
        for (segment in segments) {
            if (!segment.startsWith(':') && segment.compareTo(segments2[i], ignoreCase = true) != 0) {
                return false
            }
            i++
        }
        return true
    }
}

