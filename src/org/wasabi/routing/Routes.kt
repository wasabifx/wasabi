package org.wasabi.routing

import java.util.ArrayList
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpMethod

public object Routes {

    val routeStorage = hashMapOf<String, ArrayList<Route>>()

    private fun addRoute(method: HttpMethod, path: String, handler: (Request, Response) -> Unit) {
        //routeStorage.map(path -> Route(path, method, handler))
    }

    public fun findHandler(method: HttpMethod, path: String): (Request, Response) -> Unit {

        for (route in routeStorage) {
            if (route.matchesPath(path)) {
                if (route.method == method) {
                    return route.handler
                }
                throw MethodNotAllowedException(route.toString())
            }
        }
        throw RouteNotFoundException("Routing entry not found")

    }

    public fun get(path: String, handler: (Request, Response) -> Unit) {
        addRoute(HttpMethod.GET, path, handler)
    }

    public fun post(path: String, handler: (Request, Response) -> Unit) {
        addRoute(HttpMethod.POST, path, handler)
    }

    public fun put(path: String, handler: (Request, Response) -> Unit) {
        addRoute(HttpMethod.PUT, path, handler)
    }

    public fun head(path: String, handler: (Request, Response) -> Unit) {
        addRoute(HttpMethod.HEAD, path, handler)
    }

    public fun delete(path: String, handler: (Request, Response) -> Unit) {
        addRoute(HttpMethod.DELETE, path, handler)
    }

    public fun options(path: String, handler: (Request, Response) -> Unit) {
        addRoute(HttpMethod.OPTIONS, path, handler)
    }

    public fun getNumberOfRoutes(): Int {
        return routeStorage.size()
    }

}

fun String.get(handler: (Request, Response) -> Unit) {
    Routes.get(this, handler)
}

fun String.post(handler: (Request, Response) -> Unit) {
    Routes.post(this, handler)
}

fun String.delete(handler: (Request, Response) -> Unit) {
    Routes.delete(this, handler)
}
fun String.put(handler: (Request, Response) -> Unit) {
    Routes.put(this, handler)
}
fun String.options(handler: (Request, Response) -> Unit) {
    Routes.options(this, handler)
}
fun String.head(handler: (Request, Response) -> Unit) {
    Routes.head(this, handler)
}



