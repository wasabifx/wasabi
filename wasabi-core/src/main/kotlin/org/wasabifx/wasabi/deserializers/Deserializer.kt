package org.wasabifx.wasabi.deserializers

import java.util.HashMap

abstract class Deserializer(val mediaTypes: MutableList<String>) {
    open val name: String = this.javaClass.simpleName
    open fun canDeserialize(mediaType: String): Boolean {
        return mediaTypes.any {
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
            mediaType.matches("$it(;.*)?".toRegex(RegexOption.IGNORE_CASE))
        }
    }
    abstract fun deserialize(input: Any): HashMap<String, Any>
}
