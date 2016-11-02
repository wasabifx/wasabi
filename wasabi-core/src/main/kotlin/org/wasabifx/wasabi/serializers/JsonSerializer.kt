package org.wasabifx.wasabi.serializers

import com.fasterxml.jackson.databind.ObjectMapper

class JsonSerializer(): Serializer("application/json", "application/vnd\\.\\w*\\+json") {
    override fun serialize(input: Any): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(input)!!
    }
}