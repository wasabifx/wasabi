package org.wasabifx.wasabi.test

import io.netty.handler.codec.http.HttpMethod
import org.wasabifx.wasabi.routing.PatternAndVerbMatchingRouteLocator
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import org.junit.Test as spec


class RoutesSpecs {

    @spec fun adding_a_route_to_routing_table_should_store_it() {

        TestServer.reset()
        TestServer.appServer.get("/", { })

        assertEquals(1, TestServer.appServer.routes.size)
    }

    @spec fun finding_a_route_in_the_routing_table_should_return_route() {

        TestServer.reset()
        TestServer.appServer.get("/:id", { })

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        assertNotNull(routeLocator.findRouteHandlers("/1", HttpMethod.GET))
    }

    @spec fun finding_a_route_in_the_routing_table_by_matching_method_and_path_should_return_route() {

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

    @spec fun finding_a_route_in_the_routing_table_with_parameters_and_matching_method_should_return_route() {

        TestServer.reset()
        TestServer.appServer.post("/third", { response.send("third")})
        TestServer.appServer.get("/first/:parent/:child/ending", { response.send("")})

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val route1 = routeLocator.findRouteHandlers("/first/forest/trees/ENDING", HttpMethod.GET)

        assertNotNull(route1)

    }

    @spec fun finding_a_similar_route_in_the_routing_table_with_parameters_and_matching_method_should_return_route() {

        TestServer.reset()
        TestServer.appServer.get("/page/first", { response.send("first page")})
        TestServer.appServer.get("/page/:id", { response.send("first id")})

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val route1 = routeLocator.findRouteHandlers("/page/first", HttpMethod.GET)
        val route2 = routeLocator.findRouteHandlers("/page/smt", HttpMethod.GET)

        assertNotNull(route1)
        assertEquals("/page/first", route1.path)
        assertNotNull(route2)
        assertEquals("/page/:id", route2.path)
    }

    @spec fun finding_a_complex_route_in_the_routing_table_with_parameters_and_matching_method_should_return_route() {

        TestServer.reset()
        TestServer.appServer.get("/page/:id/view", { response.send("first page")})
        TestServer.appServer.get("/page/:id/:format", { response.send("first id")})

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val route1 = routeLocator.findRouteHandlers("/page/one/view", HttpMethod.GET)
        val route2 = routeLocator.findRouteHandlers("/page/one/json", HttpMethod.GET)

        assertNotNull(route1)
        assertEquals("/page/:id/view", route1.path)
        assertNotNull(route2)
        assertEquals("/page/:id/:format", route2.path)
    }

    @spec fun finding_a_route_in_the_routing_table_when_path_found_but_not_method_throw_exception_method_not_permitted() {

        TestServer.reset()
        TestServer.appServer.get( "/", { })
        TestServer.appServer.post( "/second", { })
        TestServer.appServer.post( "/third", { })

        val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)

        val exception = assertFails({routeLocator.findRouteHandlers("/second", HttpMethod.GET)})

        assertEquals("Invalid method exception", exception.message)

    }


    @spec fun adding_a_second_route_in_the_routing_table_with_matching_path_and_method_should_throw_exception_indicating_route_exists() {

        TestServer.reset()
        TestServer.appServer.get( "/", {})
        TestServer.appServer.get( "/a", {})
        val exception = assertFails { TestServer.appServer.get( "/", {}) }

        assertEquals("Path / with method GET already exists", exception.message)
    }

    @spec fun adding_a_non_normal_route_in_the_routing_table_then_should_normalize_the_route() {

        TestServer.reset()
        TestServer.appServer.get("unreachable", {})

        assertEquals("/unreachable", TestServer.appServer.routes.first().path)
    }

    @spec fun adding_a_second_route_in_the_routing_table_with_matching_path_param_and_method_should_throw_exception_indicating_route_exists() {

        TestServer.reset()
        TestServer.appServer.get( "/:param", {})
        val exception = assertFails { TestServer.appServer.get( "/:id", {}) }

        assertEquals("Path /:param with method GET already exists", exception.message)
    }
}

