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

Getting Started
---------------

#### The Hello World of Wasabi ####
```kotlin
  var server = AppServer()
  
  server.get("/", { response.send("Hello World!") })
  
  server.start()
```


### The AppServer ###
Each Wasabi application is composed of a single *AppServer* on which you define your handlers. A route handler can respond to any of the HTTP verbs: GET, POST, PUT, DELETE, OPTIONS, HEAD. 
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
// TODO 

### Interceptors ###
In addition to handlers, Wasabi also has interceptors. Think of interceptors as a way to add functionality to every request, or a those matching a certain route pattern.
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
* SessionManagementInterceptor: Session Management support

Most interceptor add extension methods to *AppServer* to make them easier (and more descriptive) to use. For instance the
*ContentNegotiationInterceptor* and *StaticFileInterceptor*  would be used as so

```kotlin
  val appServer = AppServer()
  
  server.negotiateContent()
  server.serveStaticFilesFromFolder("/public") 
```

## TODO ##
* Clean up code. A lot of TODO's in there. And some ugly stuff too. 
* Add missing unit tests
* Finish implementation of some things and make them production ready
* A lot of missing functionality such as support for static binding to GET/POST

Contributions
-------------
There's a lot of work still pending and any help would be appreciated. Pull Requests welcome!


