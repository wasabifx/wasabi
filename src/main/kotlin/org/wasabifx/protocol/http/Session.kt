package org.wasabifx.protocol.http

import org.joda.time.DateTime
import org.wasabifx.app.AppServer


public class Session(val id: String) {
    // TODO wire in use of config setting
    var expirationDate = DateTime.now()!!.plusSeconds(600)
    var data: Any? = null

    public fun extendSession()
    {
        expirationDate = DateTime.now()!!.plusSeconds(600)
    }
}