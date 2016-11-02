package org.wasabifx.wasabi.storage

import org.wasabifx.wasabi.protocol.http.Session
import org.wasabifx.wasabi.interceptors.SessionStorage
import java.util.concurrent.ConcurrentHashMap

class InMemorySessionStorage: SessionStorage {

    val inMemorySession = ConcurrentHashMap<String, Session>()

    override fun storeSession(session: Session) {
        inMemorySession.put(session.id, session)
    }
    override fun loadSession(sessionID: String): Session {
        return inMemorySession.get(sessionID)!!
    }
}