package org.wasabi.routing

import io.netty.handler.codec.http.HttpMethod
import org.wasabi.app.AppServer
import java.util.ArrayList

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
                if (segment.compareToIgnoreCase(segments2[i]) != 0) {
                    return false
                }
            }
            i++
        }
        return true

    }

    override fun findRoute(path: String, method: HttpMethod): Route {
        val matchingPaths = routes.filter { compareRouteSegments(it, path) }
        if (matchingPaths.count() == 0) {
            throw ResourceNotFoundException("Routing entry not found")
        }

        val matchingVerbs = (matchingPaths.filter { it.method == method })

        if (matchingVerbs.count() == 1) {
            return matchingVerbs.first!!
        }
        throw MethodNotAllowedException("Method not allowed", Array<HttpMethod>(matchingPaths.size(), { i -> matchingPaths.get(i).method}))

    }


}