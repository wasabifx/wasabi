package org.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.runner.RunWith
import org.junit.runners.Parameterized.Parameters
import java.util.ArrayList
import kotlin.test.assertNull
import kotlin.test.fails
import org.wasabi.exceptions.RouteAlreadyExistsException
import io.netty.handler.codec.http.HttpMethod
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator
import org.wasabi.test.TestServer
import org.wasabi.routing.InvalidMethodException


public class RoutesSpecs {

    spec fun adding_a_route_to_routing_table_should_store_it() {

        TestServer.reset()
        TestServer.appServer.get("/", { })

        assertEquals(1, TestServer.appServer.routes.size())
    }


    spec fun finding_a_route_in_the_routing_table_by_matching_method_and_path_should_return_route() {

        TestServer.reset()
        TestServer.appServer.get("/", { response.send("")})
        TestServer.appServer.post("/second", { response.send("second")})
        TestServer.appServer.post("/third", { response.send("third")})

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val route1 = routeLocator.findRouteHandlers("/", HttpMethod.GET)
        val route2 = routeLocator.findRouteHandlers("/third", HttpMethod.POST)

        assertNotNull(route1)
        assertNotNull(route2)
    }

    spec fun finding_a_route_in_the_routing_table_with_parameters_and_matching_method_should_return_route() {

        TestServer.reset()
        TestServer.appServer.post("/third", { response.send("third")})
        TestServer.appServer.get("/first/:parent/:child/ending", { response.send("")})

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val route1 = routeLocator.findRouteHandlers("/first/forest/trees/ENDING", HttpMethod.GET)

        assertNotNull(route1)

    }


    spec fun finding_a_route_in_the_routing_table_when_path_found_but_not_method_throw_exception_method_not_permitted() {

        TestServer.reset()
        TestServer.appServer.get( "/", { })
        TestServer.appServer.post( "/second", { })
        TestServer.appServer.post( "/third", { })

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val exception = fails({routeLocator.findRouteHandlers("/second", HttpMethod.GET)})

        assertEquals(InvalidMethodException::class.java, exception?.javaClass)

    }


    spec fun adding_a_second_route_in_the_routing_table_with_matching_path_and_method_should_throw_exception_indicating_route_exists() {

        TestServer.reset()
        TestServer.appServer.get( "/", {})
        TestServer.appServer.get( "/a", {})
        val exception = fails { TestServer.appServer.get( "/", {}) }

        assertEquals(RouteAlreadyExistsException::class.java, exception?.javaClass)
        assertEquals("Path / with method GET already exists", exception?.getMessage())
    }
}

