package org.wasabifx.wasabi.routing

import org.wasabifx.wasabi.routing.Route
import java.lang.Exception

class RouteAlreadyExistsException(existingRoute: Route): Throwable("Path ${existingRoute.path} with method ${existingRoute.method} already exists") {
}