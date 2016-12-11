package org.wasabifx.wasabi.serializers

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import org.wasabifx.wasabi.app.configuration

class XmlSerializer: Serializer(mutableListOf("text/xml", "text/vnd\\.\\w*\\+xml", "application/xml", "application/vnd\\.\\w*\\+xml")) {
    override fun serialize(input: Any): String {
        val feature = if (configuration!!.enableXML11) ToXmlGenerator.Feature.WRITE_XML_1_1 else ToXmlGenerator.Feature.WRITE_XML_DECLARATION
        val xmlMapper = XmlMapper().configure(feature, true)
        return xmlMapper.writeValueAsString(input)!!
    }
}