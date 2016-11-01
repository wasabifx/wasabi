package org.wasabifx.wasabi.serializers


public class TextPlainSerializer(): Serializer("text/plain") {
    override fun serialize(input: Any): String {
        return input.toString()
    }

}
