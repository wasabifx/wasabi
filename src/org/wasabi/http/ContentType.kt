package org.wasabi.http


public enum  class ContentType {

    fun toContentTypeString(): String {
        // TODO: Clean up.
        val str  = this.toString()
        var o = ""
        for (c in str) {
            if (c.isLowerCase()) {
                o = o + c
            } else {
                o = o + "/" + c.toString().toLowerCase()
            }
        }
        return o.trimLeading("/")
    }

    TextPlain
    TextHtml
    ApplicationJson
    ApplicationXml
}