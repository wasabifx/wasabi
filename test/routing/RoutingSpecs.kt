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
import org.wasabi.routing.RoutingException
import org.wasabi.http.HttpMethod


public class RoutingSpecs {

    spec fun adding_an_entry_to_routing_table_should_store_it() {

        val routingTable = Routes()

        routingTable.addRoute(HttpMethod.GET, "/", { request, response -> (null)})

        assertEquals(1, routingTable.routeStorage.count())
    }


    spec fun finding_a_handler_in_the_routing_table_by_matching_method_and_path_should_return_handler() {

        // Parameterized tests suck so badly in JUnit, this is a hack for now
        val routingTable = Routes()

        routingTable.addRoute(HttpMethod.GET, "/", { request, response -> (null)})
        routingTable.addRoute(HttpMethod.POST, "/second", { request, response -> (null)})
        routingTable.addRoute(HttpMethod.POST, "/third", { request, response -> (null)})
       // routingTable.addRoute(HttpMethod.POST, "/parameters/:id", { request, response -> (null)})

        val handler1 = routingTable.findHandler(HttpMethod.GET, "/")
        val handler2 = routingTable.findHandler(HttpMethod.POST, "/third")
       // val handler3 = routingTable.findHandler(HttpMethod.POST, "/parameters/2")

        assertNotNull(handler1)
        assertNotNull(handler2)
      //  assertNotNull(handler3)
    }

    spec fun finding_a_handler_in_the_routing_table_when_no_match_found_should_throw_exception() {

        val routingTable = Routes()

        routingTable.addRoute(HttpMethod.GET, "/", { request, response -> (null)})
        routingTable.addRoute(HttpMethod.POST, "/second", { request, response -> (null)})
        routingTable.addRoute(HttpMethod.POST, "/third", { request, response -> (null)})


        val exception = fails({routingTable.findHandler(HttpMethod.POST, "/secondo")})

        assertEquals(javaClass<RoutingException>(), exception.javaClass)

    }

}

