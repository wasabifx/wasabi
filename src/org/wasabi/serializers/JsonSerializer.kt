package org.wasabi.serializers

import org.codehaus.jackson.map.ObjectMapper

public class JsonSerializer: Serializer {
    override fun canSerialize(contentType: String): Boolean {
        return contentType.contains("application/json")
    }
    override fun serialize(input: Any): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(input)!!
    }
}