<!---
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
--->

### {{WebHDFS}} ###

TODO

#### WebHDFS URL Mapping ####

TODO

#### WebHDFS Examples ####

TODO


#### Assumptions

This document assumes a few things about your environment in order to simplify the examples.

* The JVM is executable as simply java.
* The Apache Knox Gateway is installed and functional.
* The example commands are executed within the context of the GATEWAY_HOME current directory.
The GATEWAY_HOME directory is the directory within the Apache Knox Gateway installation that contains the README file and the bin, conf and deployments directories.
* A few examples optionally require the use of commands from a standard Groovy installation.
These examples are optional but to try them you will need Groovy [installed|http://groovy.codehaus.org/Installing+Groovy].

h2. Customization

These examples may need to be tailored to the execution environment.
In particular hostnames and ports may need to be changes to match your environment.
In particular there are two example files in the distribution that may need to be customized.
Take a moment to review these files.
All of the values that may need to be customized can be found together at the top of each file.

* samples/ExampleWebHDFS.groovy


#### WebHDFS via KnoxShell DSL

You can use the Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar samples/ExampleWebHDFS.groovy

You can manually type in the KnoxShell DSL script into the interactive Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar

Each line from the file below will need to be typed or copied into the interactive shell.

##### samples/ExampleHdfs.groovy

    import groovy.json.JsonSlurper
    import org.apache.hadoop.gateway.shell.Hadoop
    import org.apache.hadoop.gateway.shell.hdfs.Hdfs

    gateway = "https://localhost:8443/gateway/sample"
    username = "bob"
    password = "bob-password"
    dataFile = "README"

    session = Hadoop.login( gateway, username, password )
    Hdfs.rm( session ).file( "/tmp/example" ).recursive().now()
    Hdfs.put( session ).file( dataFile ).to( "/tmp/example/README" ).now()
    text = Hdfs.ls( session ).dir( "/tmp/example" ).now().string
    json = (new JsonSlurper()).parseText( text )
    println json.FileStatuses.FileStatus.pathSuffix
    text = Hdfs.get( session ).from( "/tmp/example/README" ).now().string
    println text
    Hdfs.rm( session ).file( "/tmp/example" ).recursive().now()
    session.shutdown()


#### WebHDFS via cURL

    # 1. Optionally cleanup the sample directory in case a previous example was run without cleaning up.
    curl -i -k -u bob:bob-password -X DELETE \
        'https://localhost:8443/gateway/sample/namenode/api/v1/tmp/test?op=DELETE&recursive=true'

    # 2. Create the inode for a sample input file readme.txt in /tmp/test/input.
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sample/namenode/api/v1/tmp/test/input/README?op=CREATE'

    # 3. Upload readme.txt to /tmp/test/input.  Use the readme.txt in {GATEWAY_HOME}.
    # The sample below uses this README file found in {GATEWAY_HOME}.
    curl -i -k -u bob:bob-password -T README -X PUT \
        '{Value of Location header from command above}'

    # 4. List the contents of the output directory /tmp/test/output
    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sample/namenode/api/v1/tmp/test/input?op=LISTSTATUS'

    # 5. Optionally cleanup the test directory
    curl -i -k -u bob:bob-password -X DELETE \
        'https://localhost:8443/gateway/sample/namenode/api/v1/tmp/test?op=DELETE&recursive=true'
