package org.wasabi.routing

public class RouteNotFoundException(message: String = "Routing entry not found"): Exception(message) {
}