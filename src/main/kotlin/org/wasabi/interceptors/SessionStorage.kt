package org.wasabi.interceptors

import org.wasabi.http.Session

public interface SessionStorage {
    fun storeSession(session: Session)
    fun loadSession(sessionID: String): Session
}