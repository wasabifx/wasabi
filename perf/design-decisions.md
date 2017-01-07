
# TODO's

* Investigate The two questions from Core and resume talks in 2 weeks (20th January 2017 - TBC)
* Review GitHub issues to see current limitations people face

#Core 

* Do we use multiple or single Netty Handlers?
* Do we use outbound handlers? 
* Surface thread pooling for long-running processes
* Surface response body in interceptors? look at issue in GitHub

#Interceptors

*  Drop PreExecution Interceptor
*  Drop PostExecution Interceptor
*  Reevaluate how we run interceptors (We run once all and once per route)
*  Provide control for ordering of interceptors
*  Refactor from boolean to calling next() on interceptors or throw exception?
*  You can't currently have access to the raw body.

# Serialisers

* Rename to Jackson the existing ones
* Add support for Gson
* Sort out the Multipart form decoder mess and see how to inject this in netty
  and if in doing so, we should do our own serialisers/deserialisers the same way
  (Wrap HttpPostDecoder in our own MultipartFormData Serialiser)
* Properly deserialise JSON

# Routing

* Strongly-typed route parameters
* Strongly-typed bodies (JSON/Multipart)
  