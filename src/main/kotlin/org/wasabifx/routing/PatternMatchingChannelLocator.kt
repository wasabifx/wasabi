package org.wasabifx.routing

import org.wasabifx.protocol.websocket.Channel
import java.util.ArrayList

/**
 * Created with IntelliJ IDEA.
 * User: swishy
 * Date: 5/11/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternMatchingChannelLocator(val channels: ArrayList<Channel>) : ChannelLocator {
    override fun findChannelHandler(path: String): Channel {
        val matchingChannel = channels.filter { it.path == path }
        if (matchingChannel.count() == 0) {
            throw RouteNotFoundException()
        }

        // We should only ever have one handler for a websocket channel
        return matchingChannel.firstOrNull()!!
    }
}