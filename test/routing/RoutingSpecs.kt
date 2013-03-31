package org.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.wasabi.routing.Routes
import org.junit.runner.RunWith
import org.junit.runners.Parameterized.Parameters
import java.util.ArrayList
import kotlin.test.assertNull
import kotlin.test.fails
import org.wasabi.http.HttpMethod
import org.wasabi.routing.MethodNotAllowedException


public class RoutingSpecs {

    spec fun adding_an_entry_to_routing_table_should_store_it() {

        val routingTable = Routes()

        routingTable.get("/", { request, response -> (null)})

        assertEquals(1, routingTable.routeStorage.count())
    }


    spec fun finding_a_handler_in_the_routing_table_by_matching_method_and_path_should_return_handler() {

        // Parameterized tests suck so badly in JUnit, this is a hack for now
        val routingTable = Routes()

        routingTable.get("/", { request, response -> (response.send(""))})
        routingTable.post("/second", { request, response -> (response.send("second"))})
        routingTable.post("/third", { request, response -> (response.send("third"))})

        val handler1 = routingTable.findHandler(HttpMethod.GET, "/")
        val handler2 = routingTable.findHandler(HttpMethod.POST, "/third")

        assertNotNull(handler1)
        assertNotNull(handler2)
    }

    spec fun finding_a_handler_in_the_routing_table_when_path_found_but_not_method_throw_exception_method_not_permitted() {

        val routingTable = Routes()

        routingTable.get( "/", { request, response -> (null)})
        routingTable.post( "/second", { request, response -> (null)})
        routingTable.post( "/third", { request, response -> (null)})


        val exception = fails({routingTable.findHandler(HttpMethod.GET, "/second")})

        assertEquals(javaClass<MethodNotAllowedException>(), exception.javaClass)

    }

}

