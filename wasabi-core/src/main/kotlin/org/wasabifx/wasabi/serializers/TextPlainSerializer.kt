package org.wasabifx.wasabi.serializers


class TextPlainSerializer(): Serializer(mutableListOf("text/plain")) {
    override fun serialize(input: Any): String {
        return input.toString()
    }

}
