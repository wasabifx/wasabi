package org.wasabi.storage

import org.wasabi.http.Session
import org.wasabi.interceptors.SessionStorage
import java.util.concurrent.ConcurrentHashMap

public class InMemorySessionStorage: SessionStorage {

    val inMemorySession = ConcurrentHashMap<String, Session>()

    override fun storeSession(session: Session) {
        inMemorySession.put(session.id, session)
    }
    override fun loadSession(sessionID: String): Session {
        return inMemorySession.get(sessionID)!!
    }
}