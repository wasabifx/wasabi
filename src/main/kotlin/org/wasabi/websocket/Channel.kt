package org.wasabi.websocket

/**
 * Created with IntelliJ IDEA.
 * User: swishy
 * Date: 5/11/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
public data class Channel (val path: String, vararg val handler: ChannelHandler.() -> Unit)