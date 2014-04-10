package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.http.Session
import org.wasabi.http.Cookie
import org.wasabi.storage.SessionStorage
import org.wasabi.storage.InMemorySessionStorage
import java.util.UUID
import java.util.Date

public class SessionManagementInterceptor(val cookieKey: String = "_sessionID"): Interceptor {

    val sessionStorage = org.wasabi.app.configuration.sessionStorage

    private fun generateSessionID(): String {
        // TODO: Tie this to IP/etc.
        return UUID.randomUUID().toString()
    }

    override fun intercept(request: Request, response: Response): Boolean {
        val x = request.cookies[cookieKey]
        if (x != null && x.value != "") {
            // If we have a session bump the expiration time to keep it active
            request.session = sessionStorage.loadSession(request.session!!.id)
            request.session!!.extendSession();
            // make sure the expiry is actually persisted
            sessionStorage.storeSession(request.session!!)
            response.cookies[cookieKey] = Cookie(cookieKey, request.session!!.id, request.path, request.host, request.isSecure)
        } else {
            request.session = Session(generateSessionID())
            sessionStorage.storeSession(request.session!!)
            response.cookies[cookieKey] = Cookie(cookieKey, request.session!!.id, request.path, request.host, request.isSecure)
        }
        return true
    }
}


public fun AppServer.enableSessionSupport() {
    intercept(SessionManagementInterceptor())
}