package org.wasabi.interceptors


import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.ContentType
import org.apache.commons.codec.binary;
import io.netty.handler.codec.base64.Base64Decoder
import org.wasabi.encoding.decodeBase64
import org.wasabi.routing.RouteHandler
import org.wasabi.app.AppServer
import org.wasabi.authentication.Authentication
import org.wasabi.http.StatusCodes

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