package org.wasabi.routing

public data class RouteException(val exceptionClass: String, val handler: ExceptionHandler.() -> Unit)