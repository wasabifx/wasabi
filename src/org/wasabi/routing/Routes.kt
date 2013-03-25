package org.wasabi.routing

import java.util.ArrayList
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpMethod

public class Routes {

    val routeStorage = ArrayList<Route>()

    public fun addRoute(method: HttpMethod, path: String, handler: (Request, Response) -> Unit) {
        routeStorage.add(Route(method, path, handler))
    }

    public fun findHandler(method: HttpMethod, path: String): (Request, Response) -> Unit {

        for (route in routeStorage) {
            if (route.isMatch(method, path)) {
                return route.handler
            }
        }
        throw RoutingException("Routing entry not found")

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







}

