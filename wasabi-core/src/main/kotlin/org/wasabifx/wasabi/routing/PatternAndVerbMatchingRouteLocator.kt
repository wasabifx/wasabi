package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

// TODO: Delete this class
class PatternAndVerbMatchingRouteLocator(val routes: Set<Route>) : RouteLocator {

    override fun compareRouteSegments(routeEntry: Route, requestedPath: String): Boolean {
        val segments2 = requestedPath.split('/')
        if (routeEntry.segments.size != segments2.size) {
            return false
        }
        var i = 0
        for (segment in routeEntry.segments) {
            if (!segment.startsWith(':') && segment.compareTo(segments2[i], ignoreCase = true) != 0) {
                return false
            }
            i++
        }
        return true
    }

    override fun findRouteHandlers(requestedPath: String, requestMethod: HttpMethod): Route {
        val matchingPaths = routes.filter { compareRouteSegments(it, requestedPath) }
        if (matchingPaths.count() == 0) {
            throw RouteNotFoundException()
        }
        val matchingVerbs = (matchingPaths.filter { it.method == requestMethod })

        if (matchingVerbs.count() > 0) {
            val matchedRoute =  if (matchingVerbs.count() == 1)
                                    matchingVerbs.first()
                                else
                                    matchingVerbs.firstOrNull { it.path == requestedPath }

            return matchedRoute ?: matchingVerbs.findMostWeightyBy(requestedPath)!!
        }
        throw MethodNotAllowedException( allowedMethods = matchingVerbs.map { it.method }.toTypedArray())
    }
}