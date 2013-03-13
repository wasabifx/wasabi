

import com.hadihariri.wasabi.AppServer
import java.net.Socket
import java.net.InetSocketAddress
import org.junit.Test as test
import kotlin.test.assertEquals
import kotlin.test.fails
import java.net.ConnectException

public class AppServerStartupShutdownTests {

    test fun starting_an_app_server_should_open_the_specified_port_and_listen_for_connections() {

        val appServer = AppServer()
        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", 3000)

        appServer.start()
        socket.connect(socketAddress)
        socket.close()

        // not really required as socket would throw exception if it cannot connect
        assertEquals(true, socket.isConnected())
    }

    test fun stopping_an_app_server_should_no_longer_accept_connections() {

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
