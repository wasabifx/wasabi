package org.wasabi.routing

import org.wasabi.websocket.Channel
import java.util.ArrayList
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: swishy
 * Date: 5/11/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternMatchingChannelLocator(val channels: ArrayList<Channel>) : ChannelLocator {

    private var log = LoggerFactory.getLogger(javaClass<PatternMatchingChannelLocator>())

    override fun findChannelHandler(channel: String): Channel {

        // Handshaker has full uri on instance so we strip off the protocol and search for raw path
        val matchingChannel = channels.filter { it.path == channel.split("ws://")[1] }
        if (matchingChannel.count() == 0) {
            throw ChannelNotFoundException()
        }

        // We should only ever have one handler for a websocket channel
        return matchingChannel.first!!
    }
}