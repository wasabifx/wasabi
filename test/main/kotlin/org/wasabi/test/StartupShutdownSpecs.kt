


import java.net.Socket
import java.net.InetSocketAddress
import org.junit.Test as spec
import kotlin.test.assertEquals
import kotlin.test.fails
import java.net.ConnectException
import org.wasabi.app.AppServer
import org.wasabi.test.TestServer
import org.junit.Ignore

public class StartupShutdownSpecs {

    spec fun starting_an_app_server_should_open_the_specified_port_and_listen_for_connections() {

        TestServer.start()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", TestServer.definedPort)

        socket.connect(socketAddress)
        socket.close()

        // not really required as socket would throw exception if it cannot connect
        assertEquals(true, socket.isConnected())
        TestServer.stop()
    }

    Ignore("Server stopping has issues in tests....")
    spec fun stopping_an_app_server_should_no_longer_accept_connections() {

        TestServer.start()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", TestServer.definedPort)

        TestServer.stop()

        val exception = fails({ socket.connect(socketAddress)})
        socket.close()

        assertEquals(javaClass<ConnectException>(), exception.javaClass)

    }
}
