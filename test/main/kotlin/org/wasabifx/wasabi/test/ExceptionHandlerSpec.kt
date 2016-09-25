package org.wasabifx.wasabi.test

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.exceptionHandler
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test as spec

class ExceptionHandlerSpec: Spek({
    given("an app server") {
        beforeEach {
            TestServer.reset()
        }
        on("not setting an exception handler") {
            it("should use default exception handler") {
                assertTrue(TestServer.appServer.exceptionHandlers.isNotEmpty())
            }
        }
        on("setting an exception handler") {
            val defaultExceptionHandler = TestServer.appServer.exceptionHandlers.first()
            TestServer.appServer.exception({ response.setStatus(StatusCodes.NotImplemented) })
            it("should use the exception handler") {
                assertTrue(defaultExceptionHandler !== TestServer.appServer.exceptionHandlers.first())
            }
        }
        on("setting an exception handler") {
            val defaultExceptionHandlersCount = TestServer.appServer.exceptionHandlers.size
            TestServer.appServer.exception(Exception::class, { })
            it("should increase the number of exception handlers") {
                assertEquals(defaultExceptionHandlersCount + 1, TestServer.appServer.exceptionHandlers.size)
            }
        }
    }
})
