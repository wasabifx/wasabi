package org.wasabifx.exceptions

import org.wasabifx.routing.Route

public class RouteAlreadyExistsException(existingRoute: Route): Exception("Path ${existingRoute.path} with method ${existingRoute.method} already exists") {
}