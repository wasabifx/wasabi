import com.hadihariri.wasabi
import com.hadihariri.wasabi.HttpServer
import javax.security.auth.login.Configuration
import com.hadihariri.wasabi.AppConfiguration

fun main(args: Array<String>) {

    val server = HttpServer(AppConfiguration())

   // server.get("/", { req, res -> res.send("object")})

    server.start()
}