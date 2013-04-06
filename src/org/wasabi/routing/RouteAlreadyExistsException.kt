package org.wasabi.routing

public class RouteAlreadyExistsException(existingRoute: Route): Exception("Path ${existingRoute.path} with method ${existingRoute.method} already exists") {
}