package org.wasabi.routing

import org.slf4j.LoggerFactory
import org.wasabi.routing.exceptions.ExceptionHandlerNotFoundException
import java.util.*

/**
 * Created by condaa1 on 24/06/16.
 */
class ClassMatchingExceptionHandlerLocator(val handlers: ArrayList<RouteException>) : ExceptionHandlerLocator {

    private var log = LoggerFactory.getLogger(ClassMatchingExceptionHandlerLocator::class.java)

    override fun findExceptionHandlers(exception: Exception): RouteException {
        val matchingHandler = handlers.filter { it.exceptionClass == exception.javaClass.name}
        if (matchingHandler.count() == 0) {
            throw ExceptionHandlerNotFoundException(exception)
        }
        return matchingHandler.firstOrNull()!!
    }
}