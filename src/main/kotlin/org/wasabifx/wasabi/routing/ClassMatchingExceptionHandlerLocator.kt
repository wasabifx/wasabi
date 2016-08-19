package org.wasabifx.wasabi.routing

/**
 * Created by condaa1 on 24/06/16.
 */
class ClassMatchingExceptionHandlerLocator(val handlers: Set<RouteException>) : ExceptionHandlerLocator {

    override fun findExceptionHandlers(exception: Exception): RouteException {
        val matchingHandler = handlers.filter { it.exceptionClass == exception.javaClass.name}
        if (matchingHandler.count() == 0) {
            return handlers.filter { it.exceptionClass.isBlank() }.firstOrNull()!!
        }
        return matchingHandler.firstOrNull()!!
    }
}