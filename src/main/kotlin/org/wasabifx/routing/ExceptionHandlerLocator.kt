package org.wasabifx.routing

public interface ExceptionHandlerLocator {
    fun findExceptionHandlers(exception: Exception): RouteException
}
