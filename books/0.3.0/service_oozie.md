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

### Oozie ###

TODO

#### Oozie URL Mapping ####

TODO

#### Oozie Examples ####

TODO

##### Assumptions

This document assumes a few things about your environment in order to simplify the examples.

* The JVM is executable as simply java.
* The Apache Knox Gateway is installed and functional.
* The example commands are executed within the context of the GATEWAY_HOME current directory.
The GATEWAY_HOME directory is the directory within the Apache Knox Gateway installation that contains the README file and the bin, conf and deployments directories.
* A few examples optionally require the use of commands from a standard Groovy installation.
These examples are optional but to try them you will need Groovy [installed](http://groovy.codehaus.org/Installing+Groovy).

#### Customization

These examples may need to be tailored to the execution environment.
In particular hostnames and ports may need to be changes to match your environment.
In particular there are two example files in the distribution that may need to be customized.
Take a moment to review these files.
All of the values that may need to be customized can be found together at the top of each file.

* samples/ExampleSubmitJob.groovy
* samples/ExampleSubmitWorkflow.groovy

If you are using the Sandbox VM for your Hadoop cluster you may want to review #[Sandbox Configuration].

#### Example #2: WebHDFS & Oozie via KnoxShell DSL

This example will also submit the familiar WordCount Java MapReduce job to the Hadoop cluster via the gateway using the KnoxShell DSL.
However in this case the job will be submitted via a Oozie workflow.
There are several ways to do this depending upon your preference.

You can use the "embedded" Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar samples/ExampleSubmitWorkflow.groovy

You can manually type in the KnoxShell DSL script into the "embedded" Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar

Each line from the file below will need to be typed or copied into the interactive shell.

##### samples/ExampleSubmitWorkflow.groovy #####

    import com.jayway.jsonpath.JsonPath
    import org.apache.hadoop.gateway.shell.Hadoop
    import org.apache.hadoop.gateway.shell.hdfs.Hdfs
    import org.apache.hadoop.gateway.shell.workflow.Workflow

    import static java.util.concurrent.TimeUnit.SECONDS

    gateway = "https://localhost:8443/gateway/sandbox"
    jobTracker = "sandbox:50300";
    nameNode = "sandbox:8020";
    username = "bob"
    password = "bob-password"
    inputFile = "LICENSE"
    jarFile = "samples/hadoop-examples.jar"

    definition = """\
    <workflow-app xmlns="uri:oozie:workflow:0.2" name="wordcount-workflow">
        <start to="root-node"/>
        <action name="root-node">
            <java>
                <job-tracker>$jobTracker</job-tracker>
                <name-node>hdfs://$nameNode</name-node>
                <main-class>org.apache.hadoop.examples.WordCount</main-class>
                <arg>/tmp/test/input</arg>
                <arg>/tmp/test/output</arg>
            </java>
            <ok to="end"/>
            <error to="fail"/>
        </action>
        <kill name="fail">
            <message>Java failed</message>
        </kill>
        <end name="end"/>
    </workflow-app>
    """

    configuration = """\
    <configuration>
        <property>
            <name>user.name</name>
            <value>$username</value>
        </property>
        <property>
            <name>oozie.wf.application.path</name>
            <value>hdfs://$nameNode/tmp/test</value>
        </property>
    </configuration>
    """

    hadoop = Hadoop.login( gateway, username, password )

    println "Delete /tmp/test " + Hdfs.rm(hadoop).file( "/tmp/test" ).recursive().now().statusCode
    println "Mkdir /tmp/test " + Hdfs.mkdir(hadoop).dir( "/tmp/test").now().statusCode
    putWorkflow = Hdfs.put(hadoop).text( definition ).to( "/tmp/test/workflow.xml" ).later() {
        println "Put /tmp/test/workflow.xml " + it.statusCode }
    putData = Hdfs.put(hadoop).file( inputFile ).to( "/tmp/test/input/FILE" ).later() {
        println "Put /tmp/test/input/FILE " + it.statusCode }
    putJar = Hdfs.put(hadoop).file( jarFile ).to( "/tmp/test/lib/hadoop-examples.jar" ).later() {
        println "Put /tmp/test/lib/hadoop-examples.jar " + it.statusCode }
    hadoop.waitFor( putWorkflow, putData, putJar )

    jobId = Workflow.submit(hadoop).text( configuration ).now().jobId
    println "Submitted job " + jobId

    status = "UNKNOWN";
    count = 0;
    while( status != "SUCCEEDED" && count++ < 60 ) {
      sleep( 1000 )
      json = Workflow.status(hadoop).jobId( jobId ).now().string
      status = JsonPath.read( json, "${SDS}.status" )
    }
    println "Job status " + status;

    println "Shutdown " + hadoop.shutdown( 10, SECONDS )

    exit

#### Example #3: WebHDFS & Templeton/WebHCat via cURL

The example below illustrates the sequence of curl commands that could be used to run a "word count" map reduce job.
It utilizes the hadoop-examples.jar from a Hadoop install for running a simple word count job.
A copy of that jar has been included in the samples directory for convenience.
Take care to follow the instructions below for steps 4/5 and 6/7 where the Location header returned by the call to the NameNode is copied for use with the call to the DataNode that follows it.
These replacement values are identified with { } markup.

    # 0. Optionally cleanup the test directory in case a previous example was run without cleaning up.
    curl -i -k -u bob:bob-password -X DELETE \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test?op=DELETE&recursive=true'

    # 1. Create a test input directory /tmp/test/input
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/input?op=MKDIRS'

    # 2. Create a test output directory /tmp/test/input
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/output?op=MKDIRS'

    # 3. Create the inode for hadoop-examples.jar in /tmp/test
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/hadoop-examples.jar?op=CREATE'

    # 4. Upload hadoop-examples.jar to /tmp/test.  Use a hadoop-examples.jar from a Hadoop install.
    curl -i -k -u bob:bob-password -T samples/hadoop-examples.jar -X PUT '{Value Location header from command above}'

    # 5. Create the inode for a sample file README in /tmp/test/input
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/input/README?op=CREATE'

    # 6. Upload readme.txt to /tmp/test/input.  Use the readme.txt in {GATEWAY_HOME}.
    curl -i -k -u bob:bob-password -T README -X PUT '{Value of Location header from command above}'

    # 7. Submit the word count job via WebHCat/Templeton.
    # Take note of the Job ID in the JSON response as this will be used in the next step.
    curl -v -i -k -u bob:bob-password -X POST \
        -d jar=/tmp/test/hadoop-examples.jar -d class=wordcount \
        -d arg=/tmp/test/input -d arg=/tmp/test/output \
        'https://localhost:8443/gateway/sample/templeton/api/v1/mapreduce/jar'

    # 8. Look at the status of the job
    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sample/templeton/api/v1/queue/{Job ID returned in JSON body from previous step}'

    # 9. Look at the status of the job queue
    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sample/templeton/api/v1/queue'

    # 10. List the contents of the output directory /tmp/test/output
    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/output?op=LISTSTATUS'

    # 11. Optionally cleanup the test directory
    curl -i -k -u bob:bob-password -X DELETE \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test?op=DELETE&recursive=true'

#### Example #4: WebHDFS & Oozie via cURL

The example below illustrates the sequence of curl commands that could be used to run a "word count" map reduce job via an Oozie workflow.
It utilizes the hadoop-examples.jar from a Hadoop install for running a simple word count job.
A copy of that jar has been included in the samples directory for convenience.
Take care to follow the instructions below where replacement values are required.
These replacement values are identified with { } markup.

    # 0. Optionally cleanup the test directory in case a previous example was run without cleaning up.
    curl -i -k -u bob:bob-password -X DELETE \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test?op=DELETE&recursive=true'

    # 1. Create the inode for workflow definition file in /tmp/test
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/workflow.xml?op=CREATE'

    # 2. Upload the workflow definition file.  This file can be found in {GATEWAY_HOME}/templates
    curl -i -k -u bob:bob-password -T templates/workflow-definition.xml -X PUT \
        '{Value Location header from command above}'

    # 3. Create the inode for hadoop-examples.jar in /tmp/test/lib
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/lib/hadoop-examples.jar?op=CREATE'

    # 4. Upload hadoop-examples.jar to /tmp/test/lib.  Use a hadoop-examples.jar from a Hadoop install.
    curl -i -k -u bob:bob-password -T samples/hadoop-examples.jar -X PUT \
        '{Value Location header from command above}'

    # 5. Create the inode for a sample input file readme.txt in /tmp/test/input.
    curl -i -k -u bob:bob-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/input/README?op=CREATE'

    # 6. Upload readme.txt to /tmp/test/input.  Use the readme.txt in {GATEWAY_HOME}.
    # The sample below uses this README file found in {GATEWAY_HOME}.
    curl -i -k -u bob:bob-password -T README -X PUT \
        '{Value of Location header from command above}'

    # 7. Create the job configuration file by replacing the {NameNode host:port} and {JobTracker host:port}
    # in the command below to values that match your Hadoop configuration.
    # NOTE: The hostnames must be resolvable by the Oozie daemon.  The ports are the RPC ports not the HTTP ports.
    # For example {NameNode host:port} might be sandbox:8020 and {JobTracker host:port} sandbox:50300
    # The source workflow-configuration.xml file can be found in {GATEWAY_HOME}/templates
    # Alternatively, this file can copied and edited manually for environments without the sed utility.
    sed -e s/REPLACE.NAMENODE.RPCHOSTPORT/{NameNode host:port}/ \
        -e s/REPLACE.JOBTRACKER.RPCHOSTPORT/{JobTracker host:port}/ \
        <templates/workflow-configuration.xml >workflow-configuration.xml

    # 8. Submit the job via Oozie
    # Take note of the Job ID in the JSON response as this will be used in the next step.
    curl -i -k -u bob:bob-password -T workflow-configuration.xml -H Content-Type:application/xml -X POST \
        'https://localhost:8443/gateway/sample/oozie/api/v1/jobs?action=start'

    # 9. Query the job status via Oozie.
    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sample/oozie/api/v1/job/{Job ID returned in JSON body from previous step}'

    # 10. List the contents of the output directory /tmp/test/output
    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test/output?op=LISTSTATUS'

    # 11. Optionally cleanup the test directory
    curl -i -k -u bob:bob-password -X DELETE \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp/test?op=DELETE&recursive=true'