package org.wasabi.routing

import org.wasabi.websocket.Channel

/**
 * Created with IntelliJ IDEA.
 * User: swishy
 * Date: 5/11/13
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChannelLocator {
        fun findChannelHandler(path: String): Channel
}