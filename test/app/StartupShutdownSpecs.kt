


import java.net.Socket
import java.net.InetSocketAddress
import org.junit.Test as spec
import kotlin.test.assertEquals
import kotlin.test.fails
import java.net.ConnectException
import org.wasabi.app.AppServer

public class StartupShutdownSpecs {

    spec fun starting_an_app_server_should_open_the_specified_port_and_listen_for_connections() {

        val appServer = AppServer()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", 3000)

        appServer.start()
        socket.connect(socketAddress)
        socket.close()

        // not really required as socket would throw exception if it cannot connect
        assertEquals(true, socket.isConnected())
    }

    spec fun stopping_an_app_server_should_no_longer_accept_connections() {

        val appServer = AppServer()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", 3000)
        appServer.start()

        appServer.stop()

        val exception = fails({ socket.connect(socketAddress)})
        socket.close()

        assertEquals(javaClass<ConnectException>(), exception.javaClass)

    }
}
