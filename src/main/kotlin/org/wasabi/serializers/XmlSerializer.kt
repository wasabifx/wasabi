package org.wasabi.serializers

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator

public class XmlSerializer: Serializer("text/xml", "text/vnd\\.\\w*\\+xml", "application/xml", "application/vnd\\.\\w*\\+xml") {
    override fun serialize(input: Any): String {
        val xmlMapper = XmlMapper().configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true)
        return xmlMapper.writeValueAsString(input)!!
    }
}