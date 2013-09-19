package org.wasabi.routing

public class RouteNotFoundException(val message: String = "Routing entry not found"): Exception(message) {
}