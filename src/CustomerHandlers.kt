import org.wasabi.routing.RouteHandler
import java.util.ArrayList
import exampleserver.customerList
import org.wasabi.http.Response
import org.wasabi.http.Request
import org.wasabi.http.HttpStatusCodes
import org.wasabi.http.with
import org.wasabi.routing.routeHandler


val listCustomers = routeHandler {
    response.send(customerList)
}




val getCustomerById = routeHandler {
    val customer = customerList.find { it.id == request.routeParams["id"]?.toInt()}
    if (customer != null) {
        response.send(customer)
    } else {
        response.setHttpStatus(HttpStatusCodes.NotFound)
    }
}



public class CustomerRoutes {

    class object {

        val createCustomer = routeHandler {

        }

    }

}



