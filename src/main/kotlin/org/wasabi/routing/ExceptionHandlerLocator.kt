package org.wasabi.routing

public interface ExceptionHandlerLocator {
    fun findExceptionHandlers(exception: Exception): RouteException
}
