package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Session

public interface SessionStorage {
    fun storeSession(session: Session)
    fun loadSession(sessionID: String): Session
}