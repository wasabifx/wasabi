package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpStatusCodes

public class RouteHandler(public val request: Request, public val response: Response)  {

    var executeNext = false

    public fun next() {
        executeNext = true
    }

}

fun routeHandler(f: RouteHandler.()->Unit) = f




fun String.badabing() {
    this.toUpperCase()
}

fun abc() {

    "hello".badabing()

}

fun def(a: (Int) -> Int) {
   hhh {
       input
   }
}
class Customer(val input: String, val output: String) {



}

fun hhh(func: Customer.() -> Unit) {



}