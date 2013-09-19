package org.wasabi.exceptions

import org.wasabi.routing.Route

public class RouteAlreadyExistsException(existingRoute: Route): Exception("Path ${existingRoute.path} with method ${existingRoute.method} already exists") {
}