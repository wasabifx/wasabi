package org.wasabifx.wasabi.routing

import org.wasabifx.wasabi.protocol.websocket.Channel

/**
 * Created with IntelliJ IDEA.
 * User: swishy
 * Date: 5/11/13
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
interface ChannelLocator {
        fun findChannelHandler(path: String): Channel
}