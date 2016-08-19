package org.wasabifx.wasabi.routing

public interface ExceptionHandlerLocator {
    fun findExceptionHandlers(exception: Exception): RouteException
}
