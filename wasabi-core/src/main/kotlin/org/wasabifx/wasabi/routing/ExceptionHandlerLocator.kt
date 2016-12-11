package org.wasabifx.wasabi.routing

interface ExceptionHandlerLocator {
    fun findExceptionHandlers(exception: Throwable): RouteException
}
