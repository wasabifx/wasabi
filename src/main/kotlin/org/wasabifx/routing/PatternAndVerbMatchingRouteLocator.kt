package org.wasabifx.routing

import io.netty.handler.codec.http.HttpMethod
import java.util.*


public class PatternAndVerbMatchingRouteLocator(val routes: ArrayList<Route>): RouteLocator {


    override fun compareRouteSegments(route1: Route, path: String): Boolean {

        val segments1 = route1.path.split('/')
        val segments2 = path.split('/')
        if (segments1.size != segments2.size) {
            return false
        }
        var i = 0
        for (segment in segments1) {
            if (segment.startsWith(':')) {
                route1.params[segment.drop(1)] = segments2[i]
            } else {
                if (segment.compareTo(segments2[i], ignoreCase = true) != 0) {
                    return false
                }
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

        if (matchingVerbs.count() == 1) {
            return matchingVerbs.firstOrNull()!!
        }
        val methods = arrayOf<HttpMethod>() // TODO: This needs to be filled
        throw InvalidMethodException(allowedMethods = methods)
    }


}