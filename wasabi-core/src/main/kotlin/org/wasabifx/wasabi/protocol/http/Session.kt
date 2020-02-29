package org.wasabifx.wasabi.protocol.http

import org.joda.time.DateTime
import java.util.*


class Session(val id: String) {
    // TODO wire in use of config setting
    var expirationDate = DateTime.now()!!.plusSeconds(600)

    private var data: HashMap<String, Any?> = hashMapOf()

    fun get(name: String) : Any? {
        return data[name]
    }

    fun set(name: String, value: Any?) {
        data[name] = value
    }

    fun extendSession()
    {
        expirationDate = DateTime.now()!!.plusSeconds(600)
    }
}