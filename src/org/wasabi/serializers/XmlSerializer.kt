package org.wasabi.serializers

public class XmlSerializer: Serializer {
    override fun serialize(input: Any): String {
        throw UnsupportedOperationException()
    }
    override fun canSerialize(contentType: String): Boolean {
        return contentType.contains("/xml")
    }
}