package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.interceptors.useBasicAuthentication
import kotlin.test.assertEquals

public class BasicAuthenticationInterceptorSpecs : TestServerContext() {

    spec fun requesting_a_protected_resource_should_return_authentication_required () {

        TestServer.appServer.useBasicAuthentication("protected", { (user, pass) -> user == pass }, "/protected")

        TestServer.appServer.get("/protected", { response.send("This should be proctected")})

        val response = get("http://localhost:${TestServer.definedPort}/protected", hashMapOf())

        assertEquals(401, response.statusCode)


    }

}
    