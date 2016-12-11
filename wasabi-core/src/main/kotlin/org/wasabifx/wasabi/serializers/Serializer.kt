package org.wasabifx.wasabi.serializers

abstract class Serializer(val mediaTypes: MutableList<String>) {
    open val name : String = javaClass.simpleName
    open fun canSerialize(mediaType: String): Boolean {
        return mediaTypes.any { mediaType.matches(it.toRegex()) }
    }
    abstract fun serialize(input: Any): String
}