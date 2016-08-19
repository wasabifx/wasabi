package org.wasabifx.wasabi.interceptors


import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.authentication.Authentication
import org.wasabifx.wasabi.protocol.http.ContentType
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.StatusCodes

class AuthenticationInterceptor(val implementation: Authentication ) : Interceptor() {
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

fun AppServer.useAuthentication(implementation: Authentication, path: String = "*") {
    intercept(AuthenticationInterceptor(implementation), path)
}