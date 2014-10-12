# opencpu-clj

A Clojure library designed to use the [OpenCPU](http://opencpu.org) API from Clojure.

## Usage

The focus of this library will be to allow Clojure applications, mainly Incanter, to call arbitrary R functions and access data from R packages.
For the current list of supported API methods see [here:] (doc/endpoints.md)

### Accessing data from an R package

To access a dataset from inside an R package, the get-dataset method can be used like this.
````Clojure
(get-dataset "https://public.opencpu.org" "MASS" "Boston")
````
It returns a class of the type 'clojure.core.matrix.impl.dataset' which is used as well by Incanter.
So all methods from Incanter, which take a dataset should work with it.

### Calling R methods 

The following is a proof of concept. It will likely change in the future.

To call R, there are low-level methods (in opencpu.clj), which just call the OpenCPU endpoints
They require parameter to be encoded in JSON or to be the keys coming from previous calls.
The parameters must be named always.

#### Low-level general call of an R function
A general call to an R method looks like this:
````Clojure
(call-R-function "http://public.opencpu.org" "stats" "rnorm" {:n 10})
=>["/ocpu/tmp/x01f6261fc3/R/.val" "/ocpu/tmp/x01f6261fc3/stdout" "/ocpu/tmp/x01f6261fc3/source" "/ocpu/tmp/x01f6261fc3/console" "/ocpu/tmp/x01f6261fc3/info" "/ocpu/tmp/x01f6261fc3/files/DESCRIPTION"]
(get-key-path "http://public.opencpu.org"  "/ocpu/tmp/x01f6261fc3/R/.val" :json)
=>(0.4976 -0.3589 -0.8081 -1.4511 0.2412 0.4624 0.7201 -0.5294 0.7155 0.6794)
````
This calls the R function "rnorm" from package "stats" with parameter (n=10) on the OpenCPU server at url "http://public.opencpu.org".

It returns a session key. (Currently in the form of a list of session key urls)
Further data of the call result can be obtained by accessing the different links via the "get-key-path" method.

These links give access to different data from the call, like:

- the result of the call
- the stdout of the call
- the parameters the function was called with
- the sessionInfo() of the call
...

So to access the return value of a function, two calls are required.

#### Low-level call of R function - JSON-RPC style

An example usage to call R function "seq", with named parameters "from" and "to".
It gets called in "json-rpc style", so returns Json, which gets coerced to a Clojure data structure (in this case a vector)
 
Attention: This is not possible to do with all R functions. Some function result cannot be converted to Json by the OpenCPU server,
 and some json coming back cannot be converted to a Clojure datastructure. 
 
So in contrary to the "general" style, which always succeeds (given the parameter are ok, so R can do the call successfully),
 the Json style might fail to marshall the result back from the server.

````Clojure
(call-R-function "http://public.opencpu.org" "base" "seq" {:from 1 :to 5} :json)
=>(1 2 3 4 5)
````

An other example is some matrix calculations done in R:

````Clojure
(def m (m/matrix [[13 2][5 4]]))
(call-function-json-RPC "http://public.opencpu.org" "base" "eigen" {:x (json/write-str m)})
=> {:values [14 3], :vectors [[0.8944 -0.1961] [0.4472 0.9806]]}
````




## License

Copyright © 2014 Carsten Behring

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
