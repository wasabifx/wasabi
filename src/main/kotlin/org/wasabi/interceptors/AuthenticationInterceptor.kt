package org.wasabi.interceptors


import org.wasabi.app.AppServer
import org.wasabi.authentication.Authentication
import org.wasabi.protocol.http.ContentType
import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response
import org.wasabi.protocol.http.StatusCodes

public class AuthenticationInterceptor(val implementation: Authentication ) : Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        if (implementation.authenticate(request, response)) {
            return true
        }
        response.setStatus(StatusCodes.Unauthorized)
        response.contentType = ContentType.Companion.Text.Plain.toString()
        response.send("Authentication Failed")
        return false
    }
}

public fun AppServer.useAuthentication(implementation: Authentication, path: String = "*") {
    intercept(AuthenticationInterceptor(implementation), path)
}