package org.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

public trait RouteLocator {
    fun findRoute(path: String, method: HttpMethod): Route
}