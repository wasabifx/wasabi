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


        Routes.get("/", { })

        assertEquals(1, Routes.getNumberOfRoutes())
    }


    spec fun finding_a_handler_in_the_routing_table_by_matching_method_and_path_should_return_handler() {



        Routes.get("/", { response.send("")})
        Routes.post("/second", { response.send("second")})
        Routes.post("/third", { response.send("third")})

        val handler1 = Routes.findHandler(HttpMethod.GET, "/")
        val handler2 = Routes.findHandler(HttpMethod.POST, "/third")

        assertNotNull(handler1)
        assertNotNull(handler2)
    }

    spec fun finding_a_handler_in_the_routing_table_when_path_found_but_not_method_throw_exception_method_not_permitted() {



        Routes.get( "/", { })
        Routes.post( "/second", { })
        Routes.post( "/third", { })


        val exception = fails({Routes.findHandler(HttpMethod.GET, "/second")})

        assertEquals(javaClass<MethodNotAllowedException>(), exception.javaClass)

    }

}

