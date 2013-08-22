package org.wasabi.serializers

import org.codehaus.jackson.map.ObjectMapper

public class JsonSerializer(): Serializer("application/json", "application/vnd\\.\\w*\\+json") {
    override fun serialize(input: Any): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(input)!!
    }
}