package org.wasabi.interceptors

import org.wasabi.http.Session

public class InMemorySessionStorage: SessionStorage {

    val inMemorySession = hashMapOf<String, Session>()

    override fun storeSession(session: Session) {
        // TODO: Protect access with lock
        inMemorySession.put(session.id, session)
    }
    override fun loadSession(sessionID: String): Session {
        // TODO: Protect access with lock
        return inMemorySession.get(sessionID)!!
    }




}