package org.wasabifx.wasabi.deserializers

import java.util.HashMap
import com.fasterxml.jackson.databind.ObjectMapper


class JsonDeserializer: Deserializer(mutableListOf("application/json")) {
    // TODO: This is temp as it doesn't correctly handle x.y properties

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(input: Any): HashMap<String, Any> {
        val mapper = ObjectMapper()
        val map = mapper.readValue(input as String, HashMap::class.java)!!
        return map as HashMap<String, Any>
    }
}
