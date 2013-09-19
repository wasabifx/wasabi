package org.wasabi.serializers

public class XmlSerializer: Serializer("text/xml", "text/vnd\\.\\w*\\+xml", "application/xml", "application/vnd\\.\\w*\\+xml") {
    override fun serialize(input: Any): String {
        // TODO: Implement correctly
        return "<xml>XML? In this day an age? Really?</xml>"
    }
}