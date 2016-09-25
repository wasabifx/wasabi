package org.wasabifx.wasabi.test

import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import org.junit.Test as spec
import org.wasabifx.wasabi.deserializers.JsonDeserializer
import org.wasabifx.wasabi.deserializers.MultiPartFormDataDeserializer
import org.wasabifx.wasabi.serializers.JsonSerializer
import org.wasabifx.wasabi.serializers.XmlSerializer
import kotlin.test.assertEquals


class SerializerSpec: Spek({

    given("serialiser") {
        val jsonSerializer = JsonSerializer()
        val xmlSerializer = XmlSerializer()
        on("requesting a media type that serialiser supports") {
            it("should indicate that it supports it") {
                assertEquals(true, jsonSerializer.canSerialize("application/json"))
                assertEquals(true, jsonSerializer.canSerialize("application/vnd.wasabifx+json"))
                assertEquals(false, jsonSerializer.canSerialize("application/vnd.wasabifx+xml"))
                assertEquals(true, xmlSerializer.canSerialize("application/vnd.wasabifx+xml"))
                assertEquals(true, xmlSerializer.canSerialize("application/xml"))
                assertEquals(false, xmlSerializer.canSerialize("application/vnd.wasabifx+json"))
            }
        }
    }

    given("deserialiser") {
        val jsonDeserializer = JsonDeserializer()
        val mpDeserializer = MultiPartFormDataDeserializer()
        on("requesting a media type that deserialiser supports") {
            it("should indicate that it supports it") {
                assertEquals(true, jsonDeserializer.canDeserialize("application/json"))
                assertEquals(false, jsonDeserializer.canDeserialize("application/json-v2"))
                assertEquals(true, mpDeserializer.canDeserialize("application/x-www-form-urlencoded"))
                assertEquals(true, mpDeserializer.canDeserialize("application/X-WWW-Form-Urlencoded; charset=ISO-8859-1"))
                assertEquals(false, mpDeserializer.canDeserialize("application/x-www-form-urlencoded-v2; charset=utf-8"))
            }
        }
    }
})

