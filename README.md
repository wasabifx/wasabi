Wasabi - An HTTP Framework
========================

#### Description ####
An HTTP Framework built with [Kotlin](http://kotlin.jetbrains.org) for the JVM. 

Wasabi combines the expressivness of Kotlin, with the power of Netty and the simplicity of Express.js (and other Sinatra-inspired web frameworks)
to provide an easy to use HTTP framework

#### What it is ####
An HTTP framework that allows you to easily create back-end services for a web application or any other type of application that 
might require an HTTP API.

#### What it is not ####
It is not an MVC framework. There is no View Engine or templating language. You can combine it with client-side frameworks such 
as AngularJS or Ember. If you want a fully-fledged MVC framework in Kotlin, take a look at [Kara](http://www.karaframework.com)

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
A normal application consists of a section where you define a series of handlers for the applications followed by your handlers (i.e. your routing table). 

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

### Organization of Handlers ###

### Interceptors ###
In addition to handlers, Wasabi also has interceptors. Think of interceptors as a way to add functionality to every request, or a those matching a certain route pattern.

## TODO ##


