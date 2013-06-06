package org.wasabi.routing

import java.util.ArrayList
import java.util.HashMap
import org.wasabi.http.Request
import org.wasabi.http.Response
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention
import io.netty.handler.codec.http.HttpMethod

Retention(RetentionPolicy.RUNTIME) annotation class resource

public object Routes {

    private val routeStorage = ArrayList<Route>()

    private fun addRoute(method: HttpMethod, path: String, handler: RouteHandler.() -> Unit) {
        val existingRoute = routeStorage.filter { it.path == path && it.method == method}
        if (existingRoute.count() >= 1) {
            throw RouteAlreadyExistsException(existingRoute.first!!)
        }
        routeStorage.add(Route(path, method, HashMap<String, String>(), handler))
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

    public fun size(): Int {
        return routeStorage.size()
    }

    public fun clearAll() {
        routeStorage.clear()
    }

    public fun getAllRoutes(): ArrayList<Route> {
        return routeStorage
    }

    public fun intercept(path: String, ocurrence: InterceptorOccurence, handler: RouteHandler.() -> Unit) {

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

fun String.intercept(position: InterceptorOccurence, handler: RouteHandler.() -> Unit) {
    Routes.intercept(this, position, handler)
}

