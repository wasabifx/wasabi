package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

interface RouteLocator {
    fun findRouteHandlers(requestedPath: String, requestMethod: HttpMethod): Route
    fun compareRouteSegments(routeEntry: Route, requestedPath: String): Boolean
}