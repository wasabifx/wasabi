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
import org.wasabi.routing.RouteAlreadyExistsException


public class RoutesSpecs {

    spec fun adding_an_entry_to_routing_table_should_store_it() {

        Routes.clearAll()
        Routes.get("/", { })

        assertEquals(1, Routes.size())
    }


    spec fun finding_a_handler_in_the_routing_table_by_matching_method_and_path_should_return_handler() {



        Routes.clearAll()
        Routes.get("/", { response.send("")})
        Routes.post("/second", { response.send("second")})
        Routes.post("/third", { response.send("third")})

        val handler1 = Routes.findRouteHandler(HttpMethod.GET, "/")
        val handler2 = Routes.findRouteHandler(HttpMethod.POST, "/third")

        assertNotNull(handler1)
        assertNotNull(handler2)
    }

    spec fun finding_a_handler_in_the_routing_table_when_path_found_but_not_method_throw_exception_method_not_permitted() {



        Routes.clearAll()
        Routes.get( "/", { })
        Routes.post( "/second", { })
        Routes.post( "/third", { })


        val exception = fails({Routes.findRouteHandler(HttpMethod.GET, "/second")})

        assertEquals(javaClass<MethodNotAllowedException>(), exception.javaClass)

    }

    spec fun adding_a_second_route_in_the_routing_table_with_matching_path_and_method_should_throw_exception_indicating_route_exists() {
        Routes.clearAll()
        Routes.get( "/", {})
        Routes.get( "/a", {})
        val exception = fails { Routes.get( "/", {}) }

        assertEquals(javaClass<RouteAlreadyExistsException>(), exception.javaClass)
        assertEquals("Path / with method GET already exists", exception?.getMessage())
    }
}

