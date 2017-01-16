
# TODO's

* Investigate The two questions from Core and resume talks in 2 weeks (20th January 2017 - TBC)
* Review GitHub issues to see current limitations people face

#Core 

* Do we use multiple or single Netty Handlers? -- single 
* Do we use outbound handlers? -- No 
* Surface thread pooling for long-running processes
* Surface response body in interceptors? look at issue in GitHub

#Interceptors

*  Drop PreExecution Interceptor -- done
*  Drop PostExecution Interceptor -- done
*  Reevaluate how we run interceptors (We run once all and once per route) #112
*  Provide control for ordering of interceptors #113
*  Refactor from boolean to calling next() on interceptors or throw exception? #114
*  You can't currently have access to the raw body. #106

# Serialisers

* Rename to Jackson the existing ones -- done
* Add support for Gson #109
* Sort out the Multipart form decoder mess and see how to inject this in netty
  and if in doing so, we should do our own serialisers/deserialisers the same way
  (Wrap HttpPostDecoder in our own MultipartFormData Serialiser) #110
* Properly deserialise JSON #111

# Routing

* Strongly-typed route parameters #24
* Strongly-typed bodies (JSON/Multipart) #24
  