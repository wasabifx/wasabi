package org.wasabi.storage

import org.wasabi.http.Session
import redis.clients.jedis.Jedis
import org.codehaus.jackson.map.ObjectMapper
import redis.clients.jedis.exceptions.JedisConnectionException

/**
 * Created by swishy on 09/04/14.
 */
public class RedisSessionStorage : SessionStorage {

    // Reduce invocation overhead and memory by maintaining an instance.
    // TODO add configuration support for redis server, talk to Hadi re storage config.
    val redisContext : Jedis = Jedis("localhost")
    val mapper = ObjectMapper();

    {
        try {
            // Make sure we connect on init of storage instance.
            redisContext.connect()
        }
        catch(exception : JedisConnectionException)
        {
          // TODO add logging.
        }
    }


    override fun loadSession(sessionID: String): Session {
        var serialisedSession = redisContext.get(sessionID)
        return mapper.readValue(serialisedSession, javaClass<Session>()) as Session
    }
    override fun storeSession(session: Session) {
        redisContext.set(session.id, mapper.writeValueAsString(session))
    }
}