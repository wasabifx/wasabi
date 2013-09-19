package org.wasabi.encoding

import org.apache.commons.codec.binary.Base64


fun String.decodeBase64(encoding: String): String {
    if (encoding == "base64") {
        return String(Base64.decodeBase64(this)!!)
    } else {
        throw IllegalArgumentException()
    }
}

fun String.encodeBase64(input: String): String {
    return Base64.encodeBase64(input.toByteArray()).toString()
}