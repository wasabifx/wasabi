package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.protocol.http.Cookie
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.Session
import org.wasabifx.wasabi.storage.InMemorySessionStorage
import java.util.*

class SessionManagementInterceptor(val cookieKey: String = "_sessionID", sessionStorage: SessionStorage = InMemorySessionStorage()): Interceptor, SessionStorage by sessionStorage {

    private fun generateSessionID(): String {
        return UUID.randomUUID().toString()
    }

    override fun intercept(request: Request, response: Response): Boolean {
        if (request.cookies.containsKey(cookieKey)) {
            val cookie = request.cookies.get(cookieKey)

            if (cookie is Cookie) {
                if (cookie.value() != "") {
                    request.session = loadSession(cookie.value())
                    request.session?.let {
                        it.extendSession()
                    }
                } else {
                    request.session = Session(generateSessionID())
                    storeSession(request.session!!)
                }
            }
        }

        val tmpCookie = Cookie(cookieKey, request.session!!.id)
        tmpCookie.setDomain(request.host)
        tmpCookie.isSecure = request.isSecure
        tmpCookie.setPath("/")

        response.setCookie(tmpCookie)

        return true
    }
}


fun AppServer.enableSessionSupport() {
    intercept(SessionManagementInterceptor())
}
