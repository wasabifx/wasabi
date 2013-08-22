package org.wasabi.serializers

public class XmlSerializer: Serializer("text/xml", "text/vnd\\.\\w*\\+xml", "application/xml", "application/vnd\\.\\w*\\+xml") {
    override fun serialize(input: Any): String {
        throw UnsupportedOperationException()
    }
}