package org.wasabifx.wasabi.routing

import org.wasabifx.wasabi.routing.Route

class RouteAlreadyExistsException(existingRoute: Route): Exception("Path ${existingRoute.path} with method ${existingRoute.method} already exists") {
}