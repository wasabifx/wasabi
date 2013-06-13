import org.wasabi
import javax.security.auth.login.Configuration
import org.wasabi.http.HttpServer
import org.wasabi.app.AppConfiguration
import org.wasabi.app.AppServer
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.routing.RouteHandler
import org.wasabi.routing.*
import org.wasabi.http.ContentType
import org.wasabi.interceptors.BasicAuthenticationInterceptor

fun main(args: Array<String>) {

    val server = AppServer()


    server.configuration.enableLogging = true

    server.beforeRequestInterceptors.add(BasicAuthenticationInterceptor("secure area", { (user: String, pass: String) -> user == pass }))



                //      Why not return the function to call next().....



  //  Routes.get("*");



    Routes.get("/good", {



        response.send("Well this means that routes now work!")


    })

    "/customer/:id" get {


        response.send("Hello!")

    }


    server.start()

}








