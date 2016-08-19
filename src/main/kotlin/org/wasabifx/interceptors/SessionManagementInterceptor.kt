package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.wasabifx.app.AppServer
import org.wasabifx.protocol.http.Session
import org.wasabifx.protocol.http.Cookie
import org.wasabifx.storage.InMemorySessionStorage
import java.util.UUID
import java.util.Date

public class SessionManagementInterceptor(val cookieKey: String = "_sessionID", sessionStorage: SessionStorage = InMemorySessionStorage()): Interceptor(), SessionStorage by sessionStorage {

    private fun generateSessionID(): String {
        return UUID.randomUUID().toString()
    }

    override fun intercept(request: Request, response: Response): Boolean {
        val x = request.cookies[cookieKey]
        if (x != null && x.value != "") {
            // If we have a session bump the expiration time to keep it active
            request.session = loadSession(x.value)
            request.session!!.extendSession();
        } else {
            request.session = Session(generateSessionID())
            storeSession(request.session!!)
        }
        response.cookies[cookieKey] = Cookie(cookieKey, request.session!!.id, request.path, request.host, request.isSecure)
        return true
    }
}


public fun AppServer.enableSessionSupport() {
    intercept(SessionManagementInterceptor())
}
