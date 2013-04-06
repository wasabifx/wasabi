package org.wasabi.routing

import java.util.ArrayList
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpMethod
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention

public object Routes {

    val routeStorage = ArrayList<Route>()

    private fun addRoute(method: HttpMethod, path: String, handler: RouteHandler.() -> Unit) {
        val existingRoute = routeStorage.filter { it.path == path && it.method == method}
        if (existingRoute.count() >= 1) {
            throw RouteAlreadyExistsException(existingRoute.first!!)
        }
        routeStorage.add(Route(path, method, handler))
    }

    public fun findRouteHandler(method: HttpMethod, path: String): RouteHandler.() -> Unit {

        val matchingPaths = routeStorage.filter { it.path == path }
        if (matchingPaths.count() == 0) {
            throw RouteNotFoundException("Routing entry not found")
        }

        val matchingVerbs = (matchingPaths.filter { it.method == method })

        if (matchingVerbs.count() == 1) {
            return matchingVerbs.first!!.handler
        }
        throw MethodNotAllowedException("Method not allowed", Array<HttpMethod>(matchingPaths.size(), { i -> matchingPaths.get(i).method}))

    }

    public fun get(path: String, handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.GET, path, handler)
    }

    public fun post(path: String, handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.POST, path, handler)
    }

    public fun put(path: String, handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.PUT, path, handler)
    }

    public fun head(path: String, handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.HEAD, path, handler)
    }

    public fun delete(path: String, handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.DELETE, path, handler)
    }

    public fun options(path: String, handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.OPTIONS, path, handler)
    }

    public fun getNumberOfRoutes(): Int {
        return routeStorage.size()
    }

    public fun clearAll() {
        routeStorage.clear()
    }
}

public fun String.get(handler: RouteHandler.() -> Unit) {
    Routes.get(this, handler)
}

public fun String.post(handler: RouteHandler.() -> Unit) {
    Routes.post(this, handler)
}

public fun String.delete(handler: RouteHandler.() -> Unit) {
    Routes.delete(this, handler)
}

public fun String.put(handler: RouteHandler.() -> Unit) {
    Routes.put(this, handler)
}

public fun String.options(handler: RouteHandler.() -> Unit) {
    Routes.options(this, handler)
}

fun String.head(handler: RouteHandler.() -> Unit) {
    Routes.head(this, handler)
}



