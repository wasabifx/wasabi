package org.wasabifx.wasabi.storage

import org.wasabifx.wasabi.protocol.http.Session
import org.wasabifx.wasabi.interceptors.SessionStorage
import java.util.concurrent.ConcurrentHashMap
import org.joda.time.DateTime
import java.util.*

class InMemorySessionStorage(val cleanupPeriod: Long = 600 * 1000): SessionStorage {

    val inMemorySession = ConcurrentHashMap<String, Session>()

    init {
        kotlin.concurrent.timer("cleanup expired sessions", startAt = Date(), period = cleanupPeriod, action = {
            val currentTime = DateTime.now()
            inMemorySession
                .filter { it.value.expirationDate < currentTime }
                .map { it.value.id }
                .forEach {
                    if (inMemorySession.containsKey(it)) {
                        inMemorySession.remove(it)
                    }
                }
        })
    }

    override fun storeSession(session: Session) {
        inMemorySession.put(session.id, session)
    }
    override fun loadSession(sessionID: String): Session {
        inMemorySession.putIfAbsent(sessionID, Session(sessionID))

        return inMemorySession.get(sessionID)!!
    }
}