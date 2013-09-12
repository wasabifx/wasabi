package org.wasabi.deserializers

import java.util.HashMap
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.JsonFactory
import org.codehaus.jackson.`type`.*


public class JsonDeserializer: Deserializer("application/json", "application/json;charset=\\w*\\W*\\w*") {
    // TODO: This is temp as it doesn't correctly handle x.y properties
    override fun deserialize(input: Any): HashMap<String, String> {
        val mapper = ObjectMapper()
        val map = mapper.readValue(input as String, javaClass<HashMap<String, String>>())!!
        return map!!
    }
}
