package org.wasabifx.serializers

import com.fasterxml.jackson.databind.ObjectMapper

public class JsonSerializer(): Serializer("application/json", "application/vnd\\.\\w*\\+json") {
    override fun serialize(input: Any): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(input)!!
    }
}