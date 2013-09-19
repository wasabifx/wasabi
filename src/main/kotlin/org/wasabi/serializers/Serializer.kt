package org.wasabi.serializers

abstract public class Serializer(vararg val mediaTypes: String) {
    open fun canSerialize(mediaType: String): Boolean {
        for (supportedMediaType in mediaTypes) {
            if (mediaType.matches(supportedMediaType)) {
                return true
            }
        }
        return false
    }
    abstract fun serialize(input: Any): String
}