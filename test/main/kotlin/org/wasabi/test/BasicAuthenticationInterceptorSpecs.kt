package org.wasabi.test

import org.wasabi.authentication.BasicAuthentication
import org.wasabi.interceptors.AuthenticationInterceptor
import org.wasabi.interceptors.useAuthentication
import org.junit.Test as spec
import kotlin.test.assertEquals

public class BasicAuthenticationInterceptorSpecs : TestServerContext() {

    @spec fun requesting_a_protected_resource_should_return_authentication_required () {

        TestServer.appServer.useAuthentication(BasicAuthentication("protected", { user, pass -> user == pass }, "/protected"))

        TestServer.appServer.get("/protected", { response.send("This should be proctected")})

        val response = get("http://localhost:${TestServer.definedPort}/protected", hashMapOf())

        assertEquals(401, response.statusCode)

        TestServer.appServer.interceptors.remove(3)
    }

    @spec fun requesting_a_unprotected_resource_should_return_success () {

        TestServer.appServer.useAuthentication(BasicAuthentication("protected", { user, pass -> user == pass }, "/protected"))

        TestServer.appServer.get("/protected", { response.send("This should be proctected")})

        val response = get("http://localhost:${TestServer.definedPort}/", hashMapOf())

        assertEquals(200, response.statusCode)

        TestServer.appServer.interceptors.remove(4)
    }

}
    