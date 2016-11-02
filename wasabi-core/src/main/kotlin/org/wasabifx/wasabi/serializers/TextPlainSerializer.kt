package org.wasabifx.wasabi.serializers


class TextPlainSerializer(): Serializer("text/plain") {
    override fun serialize(input: Any): String {
        return input.toString()
    }

}
