package org.wasabi.http



// TODO: Use object chaining instead of this.
public enum  class ContentType {

    open fun convertToString(): String {
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

    All {
        override fun convertToString(): String {
            return "*/*"
        }
    }
    TextPlain
    TextHtml
    ApplicationJson
    ApplicationXml
}