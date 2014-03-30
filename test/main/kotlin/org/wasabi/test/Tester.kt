package org.wasabi.test

import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableContentNegotiation
import org.wasabi.interceptors.parseContentNegotiationHeaders

data class Customer(val id: Int, val name: String)

fun main(args: Array<String>) {


    val server = AppServer()

    server.enableContentNegotiation()
    server.parseContentNegotiationHeaders() {
        onAcceptHeader()
    }

    server.get("/", {
        response.send(Customer(1, "Joe Smith"))
    })

    server.start()


}