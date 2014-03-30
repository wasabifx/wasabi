package org.wasabi.serializers

import com.fasterxml.jackson.dataformat.xml.XmlMapper

public class XmlSerializer: Serializer("text/xml", "text/vnd\\.\\w*\\+xml", "application/xml", "application/vnd\\.\\w*\\+xml") {
    override fun serialize(input: Any): String {
        val xmlMapper = XmlMapper()
        return xmlMapper.writeValueAsString(input)!!
    }
}