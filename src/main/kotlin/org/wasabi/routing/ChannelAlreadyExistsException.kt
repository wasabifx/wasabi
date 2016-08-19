package org.wasabi.routing

import org.wasabi.protocol.websocket.Channel

/**
 * Created with IntelliJ IDEA.
 * User: swishy
 * Date: 5/11/13
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelAlreadyExistsException(existingChannel: Channel): Exception("Path ${existingChannel.path} already exists") {
}