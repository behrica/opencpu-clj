# opencpu-clj

A Clojure library designed to use the OpenCPU API from Clojure.

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

#### Low-level General call
A general call to an R method looks like this:
````Clojure
(call-R-function "http://public.opencpu.org" "stats" "rnorm" {:n 10})
=>["/ocpu/tmp/x01f6261fc3/R/.val" "/ocpu/tmp/x01f6261fc3/stdout" "/ocpu/tmp/x01f6261fc3/source" "/ocpu/tmp/x01f6261fc3/console" "/ocpu/tmp/x01f6261fc3/info" "/ocpu/tmp/x01f6261fc3/files/DESCRIPTION"]
````
This returns a session key. (Currently in the form of a list of session key urls)
Further data of the call result can be obtained by accessing the different links.
Some methods to do this will be provided soon.

#### Low-level JSON-RPC style

An example usage to call R function "seq", with named parameters "from" and "to".
It gets called in "json-rpc style", so returns Json, which the method coerces to Clojure data structure (in this case a vector)
 
Attention: This is not possible to do with all R functions. Some function result cannot be converted to Json by the OpenCPU server,
 and some json comming back cannot be converted to a Clojure datastructure. 
 
````Clojure
(call-R-function "http://public.opencpu.org" "base" "seq" {:from 1 :to 5} :json)
=>(1 2 3 4 5)
````





## License

Copyright © 2014 Carsten Behring

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
