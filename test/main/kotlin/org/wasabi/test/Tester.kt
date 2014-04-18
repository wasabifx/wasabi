package org.wasabi.test

import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableContentNegotiation
import org.wasabi.interceptors.parseContentNegotiationHeaders
import org.wasabi.http.StatusCodes
import org.wasabi.interceptors.enableAutoLocation
import org.wasabi.interceptors.enableETag

data class Customer(val id: Int, val name: String)

fun main(args: Array<String>) {


    val server = AppServer()

    server.enableContentNegotiation()
    server.parseContentNegotiationHeaders() {
        onAcceptHeader()
    }
    server.enableETag()
    server.enableAutoLocation()



    server.get("/", {
        response.send(Customer(1, "Mr. Joe Smith"))
    })



    server.post("/customer", {
        // Add this to the database
        response.resourceId = "20";
        response.setStatus(StatusCodes.Created)
    })
    server.start()


}