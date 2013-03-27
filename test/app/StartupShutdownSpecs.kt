


import java.net.Socket
import java.net.InetSocketAddress
import org.junit.Test as spec
import kotlin.test.assertEquals
import kotlin.test.fails
import java.net.ConnectException
import org.wasabi.app.AppServer
import org.wasabai.test.TestServer

public class StartupShutdownSpecs {

    spec fun starting_an_app_server_should_open_the_specified_port_and_listen_for_connections() {

        TestServer.start()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", 3000)

        socket.connect(socketAddress)
        socket.close()

        // not really required as socket would throw exception if it cannot connect
        assertEquals(true, socket.isConnected())
        TestServer.stop()
    }

    spec fun stopping_an_app_server_should_no_longer_accept_connections() {

        TestServer.start()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", 3000)

        TestServer.stop()

        val exception = fails({ socket.connect(socketAddress)})
        socket.close()

        assertEquals(javaClass<ConnectException>(), exception.javaClass)

    }
}
