package org.wasabifx.wasabi.test

import org.wasabifx.wasabi.protocol.http.Session
import org.wasabifx.wasabi.storage.InMemorySessionStorage
import kotlin.test.assertTrue
import org.junit.Test as spec

class InMemorySessionStorageSpec {

    @spec fun loading_session_with_non_existing_session_id_should_return_new_session() {
        val inMemorySessionStorage = InMemorySessionStorage()

        val session = inMemorySessionStorage.loadSession("this-id-does-not-exist-for-sure")
        assertTrue(session is Session)
    }

    @spec fun expired_sessions_should_be_removed() {
        val inMemorySessionStorage = InMemorySessionStorage(100)

        val session = inMemorySessionStorage.loadSession("this-id-does-not-exist-for-sure")
        inMemorySessionStorage.loadSession("this-id-does-not-exist-for-sure2")

        session.expirationDate = session.expirationDate.minusSeconds(session.TTL)

        Thread.sleep(600)

        assertTrue(inMemorySessionStorage.inMemorySession.count() == 1)
    }

}