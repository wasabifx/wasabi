package org.wasabi.routing

public class RouteException(val exceptionClass: String, val handler: ExceptionHandler.() -> Unit){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || javaClass !== other.javaClass) return false
        val otherRouteException = other as RouteException?
        return exceptionClass == otherRouteException?.exceptionClass
    }

    override fun hashCode(): Int = 31 * exceptionClass.hashCode()
}