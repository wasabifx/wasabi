package org.wasabi.serializers

public trait Serializer {
    fun canSerialize(contentType: String): Boolean
    fun serialize(input: Any): String
}