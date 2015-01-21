package org.wasabi.deserializers

import java.util.HashMap
import org.codehaus.jackson.map.ObjectMapper


public class JsonDeserializer: Deserializer("application/json", "application/json;\\s*charset=\\w*\\W*\\w*") {
    // TODO: This is temp as it doesn't correctly handle x.y properties
    override fun deserialize(input: Any): HashMap<String, Any> {
        val mapper = ObjectMapper()
        val map = mapper.readValue(input as String, javaClass<HashMap<String, Any>>())!!
        return map
    }
}
