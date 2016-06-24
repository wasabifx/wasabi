package org.wasabi.routing

public class HandlerAlreadyExistsException(existingHandler: RouteException): Exception("Handler for exception: ${existingHandler.exceptionClass} already exists") {
}