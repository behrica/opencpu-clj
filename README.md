# opencpu-clj

A Clojure library designed to use the [OpenCPU](http://opencpu.org) API from Clojure.

## Usage

The purpose of this library is to allow Clojure applications to call arbitrary R functions and access data from R packages via a remote opencpu server.
For the current list of supported API methods see [here] (doc/endpoints.md)

To use this library, just add

````Clojure
[clj-opencpu "0.1.0"]
````
to you project.clj


### Low level API

The low-level package contains four methods, which match the names of the API endpoints:

- object
- library
- package
- session


#### Low-level general call of an R function
To call R, there are low-level methods (in ocpu.clj), which just call the OpenCPU endpoints.
They require parameter to be encoded in JSON or to be the keys coming from previous calls.
The parameters must be named always.


A general call to an R method looks like this:
````Clojure
(object "http://public.opencpu.org" :library "stats" :R "rnorm" {:n 10})
=> {:result ["/ocpu/tmp/x047c97a725/R/.val" "/ocpu/tmp/x047c97a725/stdout" "/ocpu/tmp/x047c97a725/source" "/ocpu/tmp/x047c97a725/console" "/ocpu/tmp/x047c97a725/info" "/ocpu/tmp/x047c97a725/files/DESCRIPTION"], :status 201}
1f6261fc3/info" "/ocpu/tmp/x01f6261fc3/files/DESCRIPTION"]
(session "http://public.opencpu.org" "/ocpu/tmp/x047c97a725/R/.val" :json )
=>{:result (1.0304 -1.2131 1.1241 -0.2802 0.0636 -0.2377 0.8318 1.5895 1.9314 0.2717), :status 200}
````
This calls the R function "rnorm" from package "stats" with parameter (n=10) on the OpenCPU server at url "http://public.opencpu.org".

It returns a session key. (In the form of a list of session key links).
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

So the following will work, because the "seq" function returns a vector of numeric type, which can be marshalled to Json by R and unnmarshelled by Clojure.

````Clojure
(object "http://public.opencpu.org" :library "base" :R "seq" {:from 1 :to 5} :json)
=>{:result (1 2 3 4 5), :status 200}
````

To call the "lm" method like this, will fail, as it returns the R class "lm", which cannot be marshalled to Json by the JSONlite library used by the opencpu server.
````Clojure
(object server-url :library "stats" :R "lm" {:formula "dist ~ speed" :data "cars"} :json)
=> {:result "No method asJSON S3 class: lm\n", :status 400}
````
So this method can only be called without :json. And the resulting session objects can then be used as parameters in further calls.


An other example is some matrix calculations done in R:

````Clojure
(def m (m/matrix [[13 2][5 4]]))
(object "http://public.opencpu.org" :library "base" :R "eigen" {:x (json/write-str m)} :json)
=> {:result {:values [14 3], :vectors [[0.8944 -0.1961] [0.4472 0.9806]]}, :status 200}
````

#### Parameter format for function calls
The "parameter" map passed to the 'object' need to have the values either as:

- valid R expressions
- session keys
- Json syntax.

See the tests in ocpu_test.clj for examples.

##### Json Syntax
Json encoded parameters need to be in a Json format which is understood by the R function jsonlite::fromJSON and get converted in the correct R type.
So the "params" parameter of function 'object' is a map from Keywords to (Json-encoded) Strings, like

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
 list           | list(1,2)                         |                     | "[[1],[2]]"
 named list     | list(a=1,b=2)                     |                     | "{\"a\":[1],\"b\":[2]} "


See here for further information: http://arxiv.org/pdf/1403.2805v1.pdf

The return values for the R function calls via 'object' which requests "json" as output format, get encoded appropriately.
So the function 'object' returns a Json encoded value following the upper encodings

### High level API

#### Calling an R function

#### Evaluating an R expression


#### Accessing data from an R package

To access a dataset from inside an R package, the get-dataset method can be used like this.
````Clojure
(get-dataset "https://public.opencpu.org" "MASS" "Boston")
````
It returns a class of the type 'clojure.core.matrix.impl.dataset' which is used as well by Incanter.
So all methods from Incanter, which take a dataset should work with it.


## License

Copyright © 2014 Carsten Behring

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
