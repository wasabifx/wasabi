package org.wasabifx.interceptors


import org.wasabifx.app.AppServer
import org.wasabifx.authentication.Authentication
import org.wasabifx.protocol.http.ContentType
import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.wasabifx.protocol.http.StatusCodes

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