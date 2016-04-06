package org.wasabi.serializers

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import org.wasabi.app.configuration

public class XmlSerializer: Serializer("text/xml", "text/vnd\\.\\w*\\+xml", "application/xml", "application/vnd\\.\\w*\\+xml") {
    override fun serialize(input: Any): String {
        val feature = if (configuration!!.enableXML11) ToXmlGenerator.Feature.WRITE_XML_1_1 else ToXmlGenerator.Feature.WRITE_XML_DECLARATION
        val xmlMapper = XmlMapper().configure(feature, true)
        return xmlMapper.writeValueAsString(input)!!
    }
}