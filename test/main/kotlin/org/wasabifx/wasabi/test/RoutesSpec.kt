package org.wasabifx.wasabi.test

import io.netty.handler.codec.http.HttpMethod
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.wasabifx.wasabi.routing.PatternAndVerbMatchingRouteLocator
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull


class RoutesSpec : Spek({
    given("routing table") {
      /*  context("empty routing table") {
            TestServer.reset()
            on("defining a route") {
                TestServer.appServer.get("/", { })
                it("should add the route to the routing table") {
                    assertEquals(1, TestServer.appServer.routes.size)
                }
            }
            // TODO: This test is failing. Will pass once Spek is resolved to execute on in correct order
            on("adding an existing route") {
                TestServer.appServer.get("/", { })
                val exception = assertFails { TestServer.appServer.get("/", {}) }
                it("should indicate that route already exists") {
                    assertEquals("Path / with method GET already exists", exception.message)
                }
            }
        }
*/
        context("routing table with three routes") {
            TestServer.reset()
            TestServer.appServer.get("/", { response.send("") })
            TestServer.appServer.post("/second", { response.send("second") })
            TestServer.appServer.post("/third", { response.send("third") })
            on("locating a route in the table based on method and path") {
                val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)
                val route1 = routeLocator.findRouteHandlers("/", HttpMethod.GET)
                val route2 = routeLocator.findRouteHandlers("/third", HttpMethod.POST)
                it("should return corresponding routes") {
                    assertNotNull(route1)
                    assertNotNull(route2)
                }
            }
            on("locating a route in the table based only on path and mismatch on method") {
                val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)
                val exception = assertFails({ routeLocator.findRouteHandlers("/second", HttpMethod.GET) })
                it("should throw invalid method exception") {
                    assertEquals("Invalid method exception", exception.message)
                }
            }
        }

        context("routing table with three parametrised routes") {
            TestServer.reset()
            TestServer.appServer.post("/third", { response.send("third") })
            TestServer.appServer.get("/first/:parent/:child/ending", { response.send("") })
            on("locating a route in the table based on a provided parameter") {
                val routeLocator = PatternAndVerbMatchingRouteLocator(TestServer.routes)
                val route1 = routeLocator.findRouteHandlers("/first/forest/trees/ENDING", HttpMethod.GET)
                it("should return corresponding route") {
                    assertNotNull(route1)
                }
            }

        }

    }


})

