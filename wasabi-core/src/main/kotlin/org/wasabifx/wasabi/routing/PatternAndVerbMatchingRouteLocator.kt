package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod


class PatternAndVerbMatchingRouteLocator(val routes: Set<Route>): RouteLocator {


    override fun compareRouteSegments(route1: Route, path: String): Boolean {

        val segments1 = route1.path.split('/')
        val segments2 = path.split('/')
        if (segments1.size != segments2.size) {
            return false
        }
        var i = 0
        for (segment in segments1) {
            if (!segment.startsWith(':') &&
                            segment.compareTo(segments2[i], ignoreCase = true) != 0) {
                    return false

            }
            i++
        }
        return true
    }

    override fun findRouteHandlers(path: String, method: HttpMethod): Route {
        val matchingPaths = routes.filter { compareRouteSegments(it, path) }
        if (matchingPaths.count() == 0) {
            throw RouteNotFoundException()
        }

        val matchingVerbs = (matchingPaths.filter { it.method == method })

        if (matchingVerbs.count() > 0) {
            val matchedRoute = if (matchingVerbs.count() == 1) matchingVerbs.first()
                               else matchingVerbs.firstOrNull { it.path == path }
            return matchedRoute ?: matchingVerbs.findMostWeightyBy(path)!!
        }
        val methods = arrayOf<HttpMethod>() // TODO: This needs to be filled
        throw InvalidMethodException(allowedMethods = methods)
    }
}