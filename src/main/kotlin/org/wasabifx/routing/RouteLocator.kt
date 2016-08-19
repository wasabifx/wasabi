package org.wasabifx.routing

import io.netty.handler.codec.http.HttpMethod

public interface RouteLocator {
    fun findRouteHandlers(path: String, method: HttpMethod): Route
    fun compareRouteSegments(route1: Route, path: String): Boolean
}