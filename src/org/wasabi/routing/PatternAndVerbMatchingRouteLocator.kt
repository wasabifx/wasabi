package org.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

public class PatternAndVerbMatchingRouteLocator: RouteLocator {

    private fun compareSegments(route: Route, path2: String): Boolean {

        val segments1 = route.path.split('/')
        val segments2 = path2.split('/')
        if (segments1.size != segments2.size) {
            return false
        }
        var i = 0
        for (segment in segments1) {
            if (segment.startsWith(':')) {
                route.params[segment.drop(1)] = segments2[i]
            } else {
                if (segment.compareToIgnoreCase(segments2[i]) != 0) {
                    return false
                }
            }
            i++
        }
        return true

    }

    override fun findRoute(path: String, method: HttpMethod): Route {
        val matchingPaths = Routes.getAllRoutes().filter { compareSegments(it, path) }
        if (matchingPaths.count() == 0) {
            throw RouteNotFoundException("Routing entry not found")
        }

        val matchingVerbs = (matchingPaths.filter { it.method == method })

        if (matchingVerbs.count() == 1) {
            return matchingVerbs.first!!
        }
        throw MethodNotAllowedException("Method not allowed", Array<HttpMethod>(matchingPaths.size(), { i -> matchingPaths.get(i).method}))

    }


}