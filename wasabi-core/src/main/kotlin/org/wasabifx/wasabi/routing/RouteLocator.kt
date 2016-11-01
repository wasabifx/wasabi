package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

interface RouteLocator {
    fun findRouteHandlers(path: String, method: HttpMethod): Route
    fun compareRouteSegments(route1: Route, path: String): Boolean
}