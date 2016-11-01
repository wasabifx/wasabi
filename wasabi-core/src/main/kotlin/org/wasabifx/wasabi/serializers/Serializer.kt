package org.wasabifx.wasabi.serializers

abstract public class Serializer(vararg val mediaTypes: String) {
    open fun canSerialize(mediaType: String): Boolean {
        for (supportedMediaType in mediaTypes) {
            if (mediaType.matches(supportedMediaType.toRegex())) {
                return true
            }
        }
        return false
    }
    abstract fun serialize(input: Any): String
}