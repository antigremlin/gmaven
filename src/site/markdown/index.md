<!--

    Copyright (c) 2007-2013, the original author or authors.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
# GMaven

<img src="http://media.xircles.codehaus.org/_projects/gmaven/_logos/large.png" style="float: right;"/>

[Groovy](http://groovy.codehaus.org) integration for [Apache Maven](http://maven.apache.org).

## Supported Environments

* Java 1.6+
* Groovy 2.0+
* Groovy 2.1+
* Apache Maven 3.0+
* Apache Maven 3.1+

## Features

* Script execution
* GUI console access
* Command-line shell access

For more details please see the [groovy-maven-plugin](groovy-maven-plugin/index.html) documentation.

## No Compilation Support

GMaven 2.x no longer supports any integration for compilation of Groovy sources.  There were too many problems with
stub-generation and hooking up compliation to the proper Maven lifecycle phases to effectivly support.

For compliation integration with Maven please see the
[Groovy Eclipse Compiler](http://docs.codehaus.org/display/GROOVY/Groovy-Eclipse+compiler+plugin+for+Maven),
which is the recommended and prefered option.  If any problems are discovered with the compiler please
[report an issue](http://jira.codehaus.org/browse/GRECLIPSE).

As a fallback the [Groovy ant tasks](http://groovy.codehaus.org/Compiling+With+Maven2) can also be used
if for some reason the prefered option is not viable for the target environment.
