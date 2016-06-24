package org.wasabi.routing.exceptions

class ExceptionHandlerNotFoundException(exception: Exception) : Exception("Handler not found for Exception of type ${exception.javaClass.name}") {
}