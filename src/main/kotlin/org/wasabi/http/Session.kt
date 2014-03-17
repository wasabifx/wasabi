package org.wasabi.http

import org.joda.time.DateTime
import org.wasabi.app.AppServer


public class Session(val id: String) {
    // TODO wire in use of config setting
    var expirationDate = DateTime.now()!!.plusSeconds(600)
    var data: Any? = null

    public fun extendSession()
    {
        expirationDate = DateTime.now()!!.plusSeconds(600)
    }
}