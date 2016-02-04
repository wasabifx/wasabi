


import java.net.Socket
import java.net.InetSocketAddress
import org.junit.Test as spec
import kotlin.test.assertEquals
import org.wasabi.test.TestServer
import org.wasabi.test.TestServerContext

public class StartupShutdownSpecs : TestServerContext() {

    @spec fun starting_an_app_server_should_open_the_specified_port_and_listen_for_connections() {

        val socket = Socket()
        val socketAddress = InetSocketAddress("localhost", TestServer.definedPort)

        socket.connect(socketAddress)
        //socket.close()

        // not really required as socket would throw exception if it cannot connect
        assertEquals(true, socket.isConnected())
        socket.close()
    }


}
