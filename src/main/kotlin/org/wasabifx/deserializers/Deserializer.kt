package org.wasabifx.deserializers

import java.util.HashMap

abstract public class Deserializer(vararg val mediaTypes: String) {
    open fun canDeserialize(mediaType: String): Boolean {
        for (supportedMediaType in mediaTypes) {
            /**
             * Content type may provide a charset:
             * application/x-www-form-urlencoded; charset=utf-8
             * (we SHOULD match this)
             *
             * Or have a custom suffix:
             * application/x-www-form-urlencoded-v2
             * (we SHOULDN'T match this)
             *
             * And remember that HTTP headers are case-insensitive.
             */
            if (mediaType.matches("$supportedMediaType(;.*)?".toRegex(RegexOption.IGNORE_CASE))) {
                return true
            }
        }
        return false
    }
    abstract fun deserialize(input: Any): HashMap<String, Any>
}
