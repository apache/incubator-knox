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

### {{WebHCat}} ###

TODO

#### WebHCat URL Mapping ####

TODO

#### {{WebHCat Examples}} ####

TODO

#### Assumptions

This document assumes a few things about your environment in order to simplify the examples.

* The JVM is executable as simply java.
* The Apache Knox Gateway is installed and functional.
* The example commands are executed within the context of the GATEWAY_HOME current directory.
The GATEWAY_HOME directory is the directory within the Apache Knox Gateway installation that contains the README file and the bin, conf and deployments directories.
* A few examples optionally require the use of commands from a standard Groovy installation.
These examples are optional but to try them you will need Groovy [installed|http://groovy.codehaus.org/Installing+Groovy].

#### Customization

These examples may need to be tailored to the execution environment.
In particular hostnames and ports may need to be changes to match your environment.
In particular there are two example files in the distribution that may need to be customized.
Take a moment to review these files.
All of the values that may need to be customized can be found together at the top of each file.

* samples/ExampleSubmitJob.groovy
* samples/ExampleSubmitWorkflow.groovy

If you are using the Sandbox VM for your Hadoop cluster you may want to review [these configuration tips|Sandbox Configuration].


#### Example #1: WebHDFS & Templeton/WebHCat via KnoxShell DSL

This example will submit the familiar WordCount Java MapReduce job to the Hadoop cluster via the gateway using the KnoxShell DSL.
There are several ways to do this depending upon your preference.

You can use the "embedded" Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar samples/ExampleSubmitJob.groovy

You can manually type in the KnoxShell DSL script into the "embedded" Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar

Each line from the file below will need to be typed or copied into the interactive shell.

##### samples/ExampleSubmitJob

    import com.jayway.jsonpath.JsonPath
    import org.apache.hadoop.gateway.shell.Hadoop
    import org.apache.hadoop.gateway.shell.hdfs.Hdfs
    import org.apache.hadoop.gateway.shell.job.Job

    import static java.util.concurrent.TimeUnit.SECONDS

    gateway = "https://localhost:8443/gateway/sample"
    username = "bob"
    password = "bob-password"
    dataFile = "LICENSE"
    jarFile = "samples/hadoop-examples.jar"

    hadoop = Hadoop.login( gateway, username, password )

    println "Delete /tmp/test " + Hdfs.rm(hadoop).file( "/tmp/test" ).recursive().now().statusCode
    println "Create /tmp/test " + Hdfs.mkdir(hadoop).dir( "/tmp/test").now().statusCode

    putData = Hdfs.put(hadoop).file( dataFile ).to( "/tmp/test/input/FILE" ).later() {
        println "Put /tmp/test/input/FILE " + it.statusCode }
    putJar = Hdfs.put(hadoop).file( jarFile ).to( "/tmp/test/hadoop-examples.jar" ).later() {
         println "Put /tmp/test/hadoop-examples.jar " + it.statusCode }
    hadoop.waitFor( putData, putJar )

    jobId = Job.submitJava(hadoop) \
        .jar( "/tmp/test/hadoop-examples.jar" ) \
        .app( "wordcount" ) \
        .input( "/tmp/test/input" ) \
        .output( "/tmp/test/output" ) \
        .now().jobId
    println "Submitted job " + jobId

    done = false
    count = 0
    while( !done && count++ < 60 ) {
        sleep( 1000 )
        json = Job.queryStatus(hadoop).jobId(jobId).now().string
        done = JsonPath.read( json, "${SDS}.status.jobComplete" )
    }
    println "Done " + done

    println "Shutdown " + hadoop.shutdown( 10, SECONDS )

    exit
