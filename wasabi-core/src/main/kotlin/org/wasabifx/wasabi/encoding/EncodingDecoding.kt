package org.wasabifx.wasabi.encoding

import org.apache.commons.codec.binary.Base64
import java.lang.IllegalArgumentException


fun String.decodeBase64(encoding: String): String {
    if (encoding == "base64") {
        return String(Base64.decodeBase64(this)!!)
    } else {
        throw IllegalArgumentException()
    }
}

fun String.encodeBase64(): String {
    return Base64.encodeBase64(this.toByteArray()).toString()
}