# opencpu-clj

A Clojure library designed to use the [OpenCPU](http://opencpu.org) API from Clojure.

## Usage

The focus of this library will be to allow Clojure applications  to call arbitrary R functions and access data from R packages.
For the current list of supported API methods see [here:] (doc/endpoints.md)


The low-level package contains four methods, which match the names of teh API endpoints:

- object
- library
- package
- session

### Accessing data from an R package

To access a dataset from inside an R package, the get-dataset method can be used like this.
````Clojure
(get-dataset "https://public.opencpu.org" "MASS" "Boston")
````
It returns a class of the type 'clojure.core.matrix.impl.dataset' which is used as well by Incanter.
So all methods from Incanter, which take a dataset should work with it.

### Calling R methods


To call R, there are low-level methods (in ocpu.clj), which just call the OpenCPU endpoints
They require parameter to be encoded in JSON or to be the keys coming from previous calls.
The parameters must be named always.

#### Low-level general call of an R function

A general call to an R method looks like this:
````Clojure
(object "http://public.opencpu.org" "stats" "rnorm" {:n 10})
=>["/ocpu/tmp/x01f6261fc3/R/.val" "/ocpu/tmp/x01f6261fc3/stdout" "/ocpu/tmp/x01f6261fc3/source" "/ocpu/tmp/x01f6261fc3/console" "/ocpu/tmp/x01f6261fc3/info" "/ocpu/tmp/x01f6261fc3/files/DESCRIPTION"]
(session "http://public.opencpu.org"  "/ocpu/tmp/x01f6261fc3/R/.val" :json)
=>(0.4976 -0.3589 -0.8081 -1.4511 0.2412 0.4624 0.7201 -0.5294 0.7155 0.6794)
````
This calls the R function "rnorm" from package "stats" with parameter (n=10) on the OpenCPU server at url "http://public.opencpu.org".

It returns a session key. (In the form of a list of session key links)
Further data of the call result can be obtained by accessing the different links via the "session" method.

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

Attention: This is not possible to do with all R functions. Most function results cannot be converted to Json by the OpenCPU server,
 and some json coming back cannot be converted to a Clojure datastructure.

So in contrary to the "general" style, which always succeeds (given the parameter are ok, so R can do the call successfully),
 the Json style might fail to marshall the result back from the server.

````Clojure
(object "http://public.opencpu.org" "base" "seq" {:from 1 :to 5} :json)
=>(1 2 3 4 5)
````

An other example is some matrix calculations done in R:

````Clojure
(def m (m/matrix [[13 2][5 4]]))
(object "http://public.opencpu.org" "base" "eigen" {:x (json/write-str m)} :json)
=> {:values [14 3], :vectors [[0.8944 -0.1961] [0.4472 0.9806]]}
````

### Parameter format for function calls
The parameter passed to the 'object' need to be in Json syntax.
Specifically they need to be in a Json format which is understood by the R function jsonlite::fromJSON and get converted in the correct R type.
So the "params" parameter of function 'object' is a mapr from Keywords to (Json-encoded) Strings, like

````Clojure
{:a 10   b: [1 2 3 4 5]   c: "[\"a\",\"b\",\"c\"]}
````


Examples:

 R type         | R code                            | Clojure Type        | Json representation as Clojure String literal
----------------|-----------------------------------|-------------------- |-----------------------
                |                                   |primitive            | 1  or "1"
 numeric vector |                                   |integer vector       | "[1,2,3]"
 char vector    |                                   |String vector        | "[\"a\",\"b\",\"c\"
 matrix         | matrix(1:4,nrow=2)                |                     | "[[1,3],[2,4]]
 dataframe      | data.frame(x=c(1,2),y=c("a","b")  |                     | "[{\"x\":1,\"y\":\"a\"},{\"x\":2,\"y\":\"b\"}]"
 list           | list(1,2)                         |                     | "[[1],[2]]""
 named list     | list(a=1,b=2)                     |                     | "{"a":[1],"b":[2]} "


See here for further information: http://arxiv.org/pdf/1403.2805v1.pdf

The return values for the R function calls via 'object' which requests "json" as output format, get encoded appropriately.
So the function 'object' returns a Json encoded value following the upper encodings
## License

Copyright © 2014 Carsten Behring

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
