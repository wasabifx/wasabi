package org.wasabi.storage

import org.wasabi.http.Session

public trait SessionStorage {
    fun storeSession(session: Session)
    fun loadSession(sessionID: String): Session
}