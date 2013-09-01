package exampleserver

import org.wasabi.app.AppServer
import org.wasabi.interceptors.negotiateContent
import java.util.ArrayList
import java.util.HashMap
import org.wasabi.interceptors.serveFavIconAs


data class Customer(val id: Int, val name: String, val email: String, val country: String)

val customerList =  arrayListOf<Customer>(
        Customer(1, "Joe Smith", "joe@smith.com", "UK"),
        Customer(2, "Jack Jones", "jack@jones.com", "US"),
        Customer(3, "Maria Gonzalez", "maria@gmail.com", "Spain")
)

fun main(args: Array<String>) {



    val appServer = AppServer()

    appServer.negotiateContent()


    appServer.get("/customer", listCustomers)
    appServer.get("/customer/:id", getCustomerById)
    appServer.post("/customer", CustomerRoutes.createCustomer)

    appServer.start()
}


