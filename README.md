Wasabi - An HTTP Framework
========================

#### Description ####
An HTTP Framework built with [Kotlin](http://kotlin.jetbrains.org) for the JVM. 

Wasabi combines the conciseness and expressiveness of Kotlin, the power of Netty and the simplicity of Express.js (and other Sinatra-inspired web frameworks)
to provide an easy to use HTTP framework

#### What it is ####
An HTTP framework that allows you to easily create back-end services for a web application or any other type of application that 
might require an HTTP API.

#### What it is not ####
**It is not an MVC framework**. There is no View Engine or templating language. You can combine it with client-side frameworks such 
as AngularJS or Ember. If you want a fully-fledged MVC framework in Kotlin, take a look at [Kara](http://www.karaframework.com)

**It is not a REST framework**. Primarily because there's no such thing, and while calling it REST might sell better, it would be false. However it does provide (and will provide) 
features that allow you to create resource-orientated systems that help comply with ReSTful constraints.

#### Current Status ####
In development.

#### Disclaimer ####
A lot of the API and design of this framework is based on experience developing applications with other HTTP frameworks and the needs
that have arised. However, like any framework, until you don't have several usages of it, it's hard to know if you got it right. So while
I'm hoping that there won't be major API changes, some things might change over time.


Getting Started
---------------

#### The Hello World of Wasabi ####
```kotlin
  var server = AppServer()
  
  server.get("/", { response.send("Hello World!") })
  
  server.start()
```

#### Starting a new application ####
The easiest way to use Wasabi is with Gradle.

1. Download Gradle.

2. Create the build.gradle file and import it into your IDE.

  To create this file you can either:

  * Have Gradle generate the file for you (easiest) running the build.gradle file under tools (Recommended). To use this option
     download the file in [tools/build.gradle](tools/build.gradle) and type

     ```
       gradle 
     ```
     for more info

  * Create it manually. See the [sample for the structure](tools/sample.gradle). Make sure you fill in the TODOs



### The AppServer ###
Each Wasabi application is composed of a single *AppServer* on which you define route handlers. A route handler can respond to any of the HTTP verbs: GET, POST, PUT, DELETE, OPTIONS, HEAD. 
A normal application consists of a section where you define a series of parameters for the application, followed by your handlers (i.e. your routing table). 

```kotlin
  
  var appServer = AppServer()
  
  server.get("/customers", { .... } )
  server.post("/customer", { .... } )
  
  server.start()
```

### Route Handlers ###
In Wasabi, every request is processed by one or more route handlers. In the previous example, we are responding to a GET to "/"  with the text "Hello World!". 
You can chain route handlers. For instance, if you want to log information about a request (this is actually built-in so no need to do it manually), you could do

```kotlin
  server.get("/",
    {
      val log = Log()
      
      log.info("URI requested is ${request.uri}")
      next()
    },
    {
      response.send("Hello World!")
    }
  )
```

By calling *next()* on each handler, the processing will continue. 

All verbs on *AppServer* have the following signature

```kotlin
    public fun get(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.GET, path, *handlers)
    }
```

where you can pass one or multiple route handlers. Each one of these is an extension method to the class *RouteHandler*. This class has various properties, amongst which are
*request* and *response*. That is how you can access these properties from inside each of the functions without an explicit declaration. 



#### Route Parameters ####
Wasabi supports route parameters. Define as many parameters as you like using : followed by the name of the parameter. Access it via request.routeParams["name"]

```kotlin
   server.get("/customer/:id", { val customerId = request.routeParams["id"] } )
```

#### Query Parameters ####
Access query parameters using queryParams property of the request. 

  http://localhost:3000/customer?name=Joe
  
```kotlin
  server.get("/customer", { val customerName = request.queryParams["name"] } )
```

#### Form Parameters ####
Access form parameters using bodyParams property of the request.
```kotlin
  server.post("/customer", { val customerNameFromForm = request.bodyParams["name"] } )
```  

### Organization of Route Handlers and Application layout ###

How you layout the code for your application or group your routes depends largely on your own choice. One thing I've always been
against is forcing people to group routes per class for instance. Having said that, there are some bounds you need to stay in. 

##### Option 1 
Defining logic for each route handler inline:

```kotlin
   val appServer = AppServer()
   
   appServer.get("/customer", { response.send(customers) })
```

For very simple operations this might be ok however, it will soon become unmaintainable. 

##### Option 2  
Define route handlers as functions and reference them:

```
   val appServer = AppServer()
   
   appServer.get("/customer", getCustomers)
```

This means that your definition of route handlers pretty much becomes a routing table, which is what it should be. 

This is the preferred option. You can then group functions however way you want:

##### Grouping by file
Group similar routes in its own file. As Kotlin allows top level functions, you do not need to have a class to group functions.
As such, you could have a file name *CustomerRouteHandlers.kt* for instance with:

```kotlin
val getCustomers = routeHandler {

  response.send(customers)

}

val getCustomerById = routeHandler {
  ...
}  
```

routeHandler is a syntatic sugar to define the type of the route handler. You could also have writte that as:

```kotlin
val getCustomers : RouteHandler.() -> Unit = {

  response.send(customers)

}

val getCustomerById  : RouteHandler.() -> Unit = {
  ...
}  
```

##### Grouping by class

If for some reason you want to group by class, you can do so. Best way is to use a class object 

```kotlin
public class CustomerRoutes {

    class object {

        val createCustomer = routeHandler {

        }

    }

}
```

*Note*
Kotlin allows you to refer to a function by name using :: syntax. In principle you could also do:
```kotlin
   appServer.get("/customer", ::getCustomer)
```
and not require an explicit variable declaration for the function. However, this currently does not work with extension functions
but hopefully will in the future.


   

### Interceptors ###
In addition to handlers, Wasabi also has interceptors. These allow you to intercept a request and decide whether you
want it to continue or not (returning false would stop processing). Since you have access to both the request and response, you can 
do whatever you need. Think of interceptors as a way to add functionality to every request, or a those matching a certain route pattern.
Some frameworks have popularized the term *middleware* to refer to something that intercepts a request/response. 
I do not agree with such a broad and somewhat ambiguous term. I like to name things as close to what they actually do.
An intercpetor implements the following trait

```kotlin
  public trait Interceptor {
    fun intercept(request: Request, response: Response): Boolean
  }
```

You return true if you want the process to continue or false if you want to interrupt the request.

To add an interceptor to the application, you use

```kotlin
  server.interceptor(MyInterceptor(), path, position)
```

where path can be a specific route or *** to match all routes. Position indicates when the intercept occurs. Possible positions are

```kotlin
  public enum class InterceptOn {
      PreRequest
      PostRequest
      Error
  }
```

Out of the box, the following interceptors are available 

* BasicAuthenticationInterceptor: Basic authentication
* ContentNegotiationInterceptor: Automatic Content negotation
* FavIconInterceptor: Support for favicon
* StaticFileInterceptor: Support for serving static files
* LoggingInterceptor: Logging
* FileBasedErrorInterceptor: Customize error messages based on convention (e.g. 404.html)
* SessionManagementInterceptor: Session Management support

Most interceptor add extension methods to *AppServer* to make them easier (and more descriptive) to use. For instance the
*ContentNegotiationInterceptor* and *StaticFileInterceptor*  would be used as so

```kotlin
  val appServer = AppServer()
  
  server.negotiateContent()
  server.serveStaticFilesFromFolder("/public") 
```
## Content Negotiation ##
Wasabi ships with content negotiation out of the box, via a couple of interceptors. In particular:

 * ContentNegotiationParserInterceptor (still looking for a better name)
   Allows you to indicate to Wasabi to not only take into account Accept Headers but also Query Fields and Extensions on documents

 * ContentNegotiationInterceptor
   Does the actual content negotiation, finding the appropriate serializer.

### How it works ###

#### Content Negotiation Parsing ####
Sometimes you want to not only do Content Negotiation using the Accept headers, but also using a query field (for instance *format=json*)
or extensions to documents (e.g. /customer.json).

The ContentNegotiationParser allows you to do this. Easiest way to use it is via the extension function:

```kotlin
   server.parseContentNegotiationHeaders() {
       onQueryParameter("format")
       onExtension()
       onAcceptHeader()
   }

```

Based on the order in which you pass in onAcceptHeader, onExtension and onQueryParameter defines the priority. Above for instance
first the Query Parameter is taking into account, then extension and lastly the Accept Header

*QueryParameter* defaults to the query parameter *format* but you can pass in a different one. Both *onExtension* and *onQueryParameter*
also take a list of mappings, which an array of extension to media type. By default *json* maps to *application/json* and *xml* to *application/xml*

#### Automatic Content Negotiation ####
By default Content Negotiation is enabled. You can disable it via the AppConfiguration. You can then just send and object you want and Wasabi
will automatically serialize it and send it back to the client.

```kotlin
   server.get("/customer/:id", {


        val customer = getCustomerById(request.params["id"])

        response.send(customer)
   }
```

Wasabi will automatically serialize that into Json, Xml or any other media type supported (see Serializers below)

#### Manual ####
If you need to manually override Content Negotiation, you can do so using the *negotiate* function on *response*

```kotlin
   server.get("/customer/:id", {


        val customer = getCustomerById(request.params["id"])

        response.negotiate (
            "text/html" with { send("Joe Smith") },
            "application/json" with { send(customer) }
        }


}
```

*negotiate* signature is:

```kotlin
    public fun negotiate(vararg negotiations: Pair<String, Response.() -> Unit>)
```

You can pass in an unlimited number of media type, functions. Also, as the function is an extension function to *Response*, you have
access to the response functions directly. That is why we can just write *send* as opposed to *response.send*.

Note that even if you use manual content negotiation, Wasabi will still try and serialize the object for you based on the media type.

### Serializers ###

Both manual as well as automatic Content Negotiation use serializers. Wasabi ships with Json and XML (TODO) serializers. These are
defined as a property of the *AppServer*.

Each Serializer can take as parameters a variable number of strings which define regular expressions for the media types it can handle. JsonSerializer
for instance takes:

```kotlin
public class JsonSerializer(): Serializer("application/json", "application/vnd\\.\\w*\\+json")
```

## CORS Support ##
Wasabi provides CORS support via an Interceptor. You can enable CORS support in multiple ways:

* Via AppConfiguration.enableCORSGlobally - which enables CORS for all routes, all verbs, all origins
* Programatically using server.enableCORSGlobally - same as above
* Programatically using server.enablesCORS([...COREntry...]) - providing an array of CORSEntry's

Each CORSEntry consists of:

```kotlin
public class CORSEntry(val path: String = "*",
                       val origins: String = "*",
                       val methods: String = "GET, POST, PUT, DELETE",
                       val headers: String = "Origin, X-Requested-With, Content-Type, Accept",
                       val credentials: String = "",
                       val preflightMaxAge: String = "") {

}
```

with the corresponding defaults. Obviously you should only have one default. 

## Auto Options Support ##
Wasabi can automatically respond to OPTIONS for a specific path. You can enable this in multiple ways:

* Via AppConfiguration.enableAutoOptions 
* Programatically using server.enableAutoOptions - same as above

## Support ##
There is a [Google Groups](https://groups.google.com/forum/#!forum/wasabifx) where we can provide support and have discussions on future directions, etc. 

## TODO ##
Full list of issues logged on [YouTrack](http://hhariri.cloudapp.net:8080/issues/WA)



Contributions
-------------
There's a lot of work still pending and any help would be appreciated. Pull Requests welcome! 

We have the project building on TeamCity.


(http://teamcity.jetbrains.com/app/rest/builds/buildType:(id:Wasabi_FullBuild)/statusIcon)

License
-------
Licensed under Apache 2 OSS License
