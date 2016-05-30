package org.wasabi.test

import org.wasabi.deserializers.JsonDeserializer
import org.wasabi.deserializers.MultiPartFormDataDeserializer
import org.junit.Test as spec
import org.wasabi.serializers.JsonSerializer
import org.wasabi.serializers.XmlSerializer
import kotlin.test.assertEquals

public class SerializerSpecs {

    @spec fun canSerialize_should_return_true_when_given_a_media_type_that_serializer_can_process() {

        val jsonSerializer = JsonSerializer()
        val xmlSerializer = XmlSerializer()

        assertEquals(true, jsonSerializer.canSerialize("application/json"))
        assertEquals(true, jsonSerializer.canSerialize("application/vnd.wasabi+json"))
        assertEquals(false, jsonSerializer.canSerialize("application/vnd.wasabi+xml"))
        assertEquals(true, xmlSerializer.canSerialize("application/vnd.wasabi+xml"))
        assertEquals(true, xmlSerializer.canSerialize("application/xml"))
        assertEquals(false, xmlSerializer.canSerialize("application/vnd.wasabi+json"))

    }

    @spec fun canDeserialize_should_return_true_when_given_a_media_type_that_deserializer_can_process() {

        val jsonDeserializer = JsonDeserializer()
        val mpDeserializer = MultiPartFormDataDeserializer()

        assertEquals(true, jsonDeserializer.canDeserialize("application/json"))
        assertEquals(false, jsonDeserializer.canDeserialize("application/json-v2"))
        assertEquals(true, mpDeserializer.canDeserialize("application/x-www-form-urlencoded"))
        assertEquals(true, mpDeserializer.canDeserialize("application/X-WWW-Form-Urlencoded; charset=ISO-8859-1"))
        assertEquals(false, mpDeserializer.canDeserialize("application/x-www-form-urlencoded-v2; charset=utf-8"))

    }

}
