package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.http.Session
import org.wasabi.http.Cookie
import java.util.UUID
import java.util.Date
import org.wasabi.storage.SessionStorage
import org.wasabi.storage.InMemorySessionStorage

public class SessionManagementInterceptor(val cookieKey: String = "_sessionID", sessionStorage: SessionStorage = InMemorySessionStorage()): Interceptor(), SessionStorage by sessionStorage {

    private fun generateSessionID(): String {
        // TODO: Tie this to IP/etc.
        return UUID.randomUUID().toString()
    }

    override fun intercept(request: Request, response: Response) {
        val x = request.cookies[cookieKey]
        if (x != null && x.value != "") {
            // If we have a session bump the expiration time to keep it active
            request.session = loadSession(request.session!!.id)
            request.session!!.extendSession();
        } else {
            request.session = Session(generateSessionID())
            storeSession(request.session!!)
            response.cookies[cookieKey] = Cookie(cookieKey, request.session!!.id, request.path, request.host, request.isSecure)
        }
        next()
    }
}


public fun AppServer.enableSessionSupport() {
    intercept(SessionManagementInterceptor())
}