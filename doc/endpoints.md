#Status of support for opencpu API methods

The "supported = yes" in this table means, that there is a method to access the data "as-is" from that endpoint.

## The API Libraries
endpoint | supported | description
----------|-----------|------------
/ocpu/library/{pkgname}/ |	|R packages installed in one of the global libraries on the server.
/ocpu/user/{username}/library/{pkgname}/||	R packages installed in the home library of Linux user {username}.
/ocpu/tmp/{key}/|yes|	Temporary sessions, which hold outputs from a function/script RPC.
/ocpu/cran/{pkgname}/||	Interfaces to the R package {pkgname} that is current on CRAN.
/ocpu/bioc/{pkgname}/||	Interfaces to the R package {pkgname} that is current on BioConductor.
/ocpu/github/{gituser}/{pkgname}/||	Interfaces to R package {pkgname} in the master branch of the identically named repository from github user {gituser}.
/ocpu/gist/{gituser}/{path}	||Static files and directories located in the gist repository from github user gituser. This API does not support packages, just files (scripts, html, etc)


## The R package API
endpoint | supported | description
----------|-----------|------------
../{pkgname}/info |yes|	Show information about this package.
../{pkgname}/R/	|| R objects exported by this package. See R object API.
../{pkgname}/data/	| yes| Data included with this package. Datasets are objects, see R object API.
../{pkgname}/man/	|yes| Manuals (help pages) included in this package.
../{pkgname}/man/{topic}/{format}	|yes| Retrieve help page about topic in output format format. Manual format must be one of text, html, pdf
../{pkgname}/html	|| Simulates the R-base html help pages (for backward compatibility).
../{pkgname}/*	||For all else, interfaces to the files in the package installation directory.

## The R object API
endpoint | supported | description
----------|-----------|------------
../R/|yes, via "packge"|	List R objects in this package or session. 
../data/|yes,via "package"|	List data objects in a package.
../{R or data}/{object}|yes|	Read object in default format. If object is a function, it can be called using HTTP POST.
../{R or data}/{object}/{format}|yes|	Retrieve an R object in a particular output format (see section on output formats).

##The R session API
endpoint | supported | description
----------|-----------|------------
/ocpu/tmp/{key}/	|yes| List available output for this session.
/ocpu/tmp/{key}/R	|yes|R objects stored in this session. Interface using R object API, same as objects in packages.
/ocpu/tmp/{key}/graphics/	|yes| Graphics (plots) stored in this session.
/ocpu/tmp/{key}/graphics/{n}/{format}|yes|	Retrieve plot number {n} in output format {format}. Format is usually one of png, pdf or svg. The {n} is an integer or "last".
/ocpu/tmp/{key}/source	|yes| Reads the input source code for this session.
/ocpu/tmp/{key}/stdout	|yes|Shows text printed to STDOUT in this session.
/ocpu/tmp/{key}/console	|yes| Shows the console input/output for this session (combines source and stdout)
/ocpu/tmp/{key}/zip	|yes|Download the entire session as a zip archive.
/ocpu/tmp/{key}/tar	|yes|Download the entire session as a gzipped tarball.
/ocpu/tmp/{key}/files/*	|yes| Interfaces to the file API in the working dir of the session.