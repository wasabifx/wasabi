package org.wasabifx.wasabi.test

import org.wasabifx.wasabi.authentication.BasicAuthentication
import org.wasabifx.wasabi.interceptors.AuthenticationInterceptor
import org.wasabifx.wasabi.interceptors.useAuthentication
import kotlin.test.assertEquals
import org.junit.Test as spec

class BasicAuthenticationInterceptorSpecs : TestServerContext() {

    @spec fun requesting_a_protected_resource_should_return_authentication_required () {

        TestServer.appServer.useAuthentication(BasicAuthentication("protected", { user, pass -> user == pass }), "/protected")

        TestServer.appServer.get("/protected", { response.send("This should be protected")})

        val response = get("http://localhost:${TestServer.definedPort}/protected", hashMapOf())

        assertEquals(401, response.statusCode)
    }

    @spec fun requesting_a_unprotected_resource_should_return_success () {

        TestServer.appServer.useAuthentication(BasicAuthentication("protected", { user, pass -> user == pass }),"/protected")

        TestServer.appServer.get("/protected", { response.send("This should be proctected")})

        TestServer.appServer.get("/notprotected", { response.send("This should not be protected")})

        val response = get("http://localhost:${TestServer.definedPort}/notprotected", hashMapOf())

        assertEquals(200, response.statusCode)
    }

}
