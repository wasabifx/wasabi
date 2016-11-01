package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.protocol.http.Session

interface SessionStorage {
    fun storeSession(session: Session)
    fun loadSession(sessionID: String): Session
}