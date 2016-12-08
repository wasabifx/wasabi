package org.wasabifx.wasabi.test

import org.apache.http.message.BasicNameValuePair
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.exceptionHandler
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test as spec

/**
 * @author Tradunsky V.V.
 */
class ExceptionHandlerSpecs : TestServerContext() {

    @spec fun should_be_default_exception_handler() {
        assertTrue(TestServer.appServer.exceptionHandlers.isNotEmpty())
    }

    @spec fun adding_the_default_exception_handler_should_override_existing_ones() {
        val defaultExceptionHandler = TestServer.appServer.exceptionHandlers.first()

        TestServer.appServer.exception({ response.setStatus(StatusCodes.NotImplemented) })

        assertTrue(defaultExceptionHandler !== TestServer.appServer.exceptionHandlers.first())
    }

    @spec fun adding_an_exception_handler_should_increase_exception_handlers_count() {
        val defaultExceptionHandlersCount = TestServer.appServer.exceptionHandlers.size

        TestServer.appServer.exception(Exception::class, { })

        assertEquals(defaultExceptionHandlersCount + 1, TestServer.appServer.exceptionHandlers.size)
    }

    @spec fun repeatedly_adding_an_exception_handler_should_override_existing_ones() {
        val defaultExceptionHandlersCount = TestServer.appServer.exceptionHandlers.size
        val expectedExceptionHandler = exceptionHandler { response.setStatus(418, "I'm a teapot") }

        TestServer.appServer.exception(Exception::class, { })
        TestServer.appServer.exception(Exception::class, expectedExceptionHandler)

        val actualExceptionHandler = TestServer.appServer.exceptionHandlers.filter { it.exceptionClass == Exception::class.java.name }.last()
        assertEquals(defaultExceptionHandlersCount + 1, TestServer.appServer.exceptionHandlers.size)
        assertTrue(expectedExceptionHandler === actualExceptionHandler.handler)
    }

    @spec fun sending_invalid_http_body_should_be_processed_with_default_exception_response(){
        TestServer.appServer.exception { assertNotNull(exception) }
        post("http://localhost:${TestServer.definedPort}/body", hashMapOf(Pair("Content-Type", "application/json")),
                arrayListOf(BasicNameValuePair("key", "\"invalid value")))
    }
}