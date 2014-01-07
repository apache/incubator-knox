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

### WebHDFS ###

REST API access to HDFS in a Hadoop cluster is provided by WebHDFS.
The [WebHDFS REST API](http://hadoop.apache.org/docs/stable/webhdfs.html) documentation is available online.
WebHDFS must be enabled in the hdfs-site.xml configuration file.
In sandbox this configuration file is located at /etc/hadoop/conf/hdfs-site.xml.
Note the properties shown below as they are related to configuration required by the gateway.
Some of these represent the default values and may not actually be present in hdfs-site.xml.

    <property>
        <name>dfs.webhdfs.enabled</name>
        <value>true</value>
    </property>
    <property>
        <name>dfs.namenode.rpc-address</name>
        <value>sandbox.hortonworks.com:8020</value>
    </property>
    <property>
        <name>dfs.namenode.http-address</name>
        <value>sandbox.hortonworks.com:50070</value>
    </property>
    <property>
        <name>dfs.https.namenode.https-address</name>
        <value>sandbox.hortonworks.com:50470</value>
    </property>

The values above need to be reflected in each topology descriptor file deployed to the gateway.
The gateway by default includes a sample topology descriptor file `{GATEWAY_HOME}/deployments/sandbox.xml`.
The values in this sample are configured to work with an installed Sandbox VM.

    <service>
        <role>NAMENODE</role>
        <url>hdfs://localhost:8020</url>
    </service>
    <service>
        <role>WEBHDFS</role>
        <url>http://localhost:50070/webhdfs</url>
    </service>

The URL provided for the role NAMENODE does not result in an endpoint being exposed by the gateway.
This information is only required so that other URLs can be rewritten that reference the Name Node's RPC address.
This prevents clients from needed to be aware of the internal cluster details.

By default the gateway is configured to use the HTTP endpoint for WebHDFS in the Sandbox.
This could alternatively be configured to use the HTTPS endpoint by provided the correct address.

#### WebHDFS URL Mapping ####

For Name Node URLs, the mapping of Knox Gateway accessible WebHDFS URLs to direct WebHDFS URLs is simple.

| ------- | ----------------------------------------------------------------------------- |
| Gateway | `https://{gateway-host}:{gateway-port}/{gateway-path}/{cluster-name}/webhdfs` |
| Cluster | `http://{webhdfs-host}:50070/webhdfs`                                         |

However, there is a subtle difference to URLs that are returned by WebHDFS in the Location header of many requests.
Direct WebHDFS requests may return Location headers that contain the address of a particular Data Node.
The gateway will rewrite these URLs to ensure subsequent requests come back through the gateway and internal cluster details are protected.

A WebHDFS request to the Node Node to retrieve a file will return a URL of the form below in the Location header.

    http://{datanode-host}:{data-node-port}/webhdfs/v1/{path}?...

Note that this URL contains the newtwork location of a Data Node.
The gateway will rewrite this URL to look like the URL below.

    https://{gateway-host}:{gateway-port}/{gateway-path}/{custer-name}/webhdfs/data/v1/{path}?_={encrypted-query-parameters}

The `{encrypted-query-parameters}` will contain the `{datanode-host}` and `{datanode-port}` information.
This information along with the original query parameters are encrypted so that the internal Hadoop details are protected.

#### WebHDFS Examples ####

The examples below upload a file, download the file and list the contents of the directory.

##### WebHDFS via client DSL

You can use the Groovy example scripts and interpreter provided with the distribution.

    java -jar bin/shell.jar samples/ExampleWebHdfsPutGet.groovy
    java -jar bin/shell.jar samples/ExampleWebHdfsLs.groovy

You can manually type the client DSL script into the KnoxShell interactive Groovy interpreter provided with the distribution.
The command below starts the KnoxShell in interactive mode.

    java -jar bin/shell.jar

Each line below could be typed or copied into the interactive shell and executed.
This is provided as an example to illustrate the use of the client DSL.

    // Import the client DSL and a useful utilities for working with JSON.
    import org.apache.hadoop.gateway.shell.Hadoop
    import org.apache.hadoop.gateway.shell.hdfs.Hdfs
    import groovy.json.JsonSlurper

    // Setup some basic config.
    gateway = "https://localhost:8443/gateway/sandbox"
    username = "guest"
    password = "guest-password"

    // Start the session.
    session = Hadoop.login( gateway, username, password )

    // Cleanup anything leftover from a previous run.
    Hdfs.rm( session ).file( "/user/guest/example" ).recursive().now()

    // Upload the README to HDFS.
    Hdfs.put( session ).file( "README" ).to( "/user/guest/example/README" ).now()

    // Download the README from HDFS.
    text = Hdfs.get( session ).from( "/user/guest/example/README" ).now().string
    println text

    // List the contents of the directory.
    text = Hdfs.ls( session ).dir( "/user/guest/example" ).now().string
    json = (new JsonSlurper()).parseText( text )
    println json.FileStatuses.FileStatus.pathSuffix

    // Cleanup the directory.
    Hdfs.rm( session ).file( "/user/guest/example" ).recursive().now()

    // Clean the session.
    session.shutdown()


##### WebHDFS via cURL

Use can use cURL to directly invoke the REST APIs via the gateway.

###### Optionally cleanup the sample directory in case a previous example was run without cleaning up.

    curl -i -k -u guest:guest-password -X DELETE \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/user/guest/example?op=DELETE&recursive=true'

###### Register the name for a sample file README in /user/guest/example.

    curl -i -k -u guest:guest-password -X PUT \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/user/guest/example/README?op=CREATE'

###### Upload README to /user/guest/example.  Use the README in {GATEWAY_HOME}.

    curl -i -k -u guest:guest-password -T README -X PUT \
        '{Value of Location header from command above}'

###### List the contents of the directory /user/guest/example.

    curl -i -k -u guest:guest-password -X GET \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/user/guest/example?op=LISTSTATUS'

###### Request the content of the README file in /user/guest/example.

    curl -i -k -u guest:guest-password -X GET \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/user/guest/example/README?op=OPEN'

###### Read the content of the file.

    curl -i -k -u guest:guest-password -X GET \
        '{Value of Location header from command above}'

###### Optionally cleanup the example directory.

    curl -i -k -u guest:guest-password -X DELETE \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/user/guest/example?op=DELETE&recursive=true'


##### WebHDFS client DSL

###### get() - Get a file from HDFS (OPEN).

* Request
    * from( String name ) - The full name of the file in HDFS.
    * file( String name ) - The name name of a local file to create with the content.
    If this isn't specified the file content must be read from the response.
* Response
    * BasicResponse
    * If file parameter specified content will be streamed to file.
* Example
    * `Hdfs.get( session ).from( "/user/guest/example/README" ).now().string`

###### ls() - Query the contents of a directory (LISTSTATUS)

* Request
    * dir( String name ) - The full name of the directory in HDFS.
* Response
    * BasicResponse
* Example
    * `Hdfs.ls( session ).dir( "/user/guest/example" ).now().string`

###### mkdir() - Create a directory in HDFS (MKDIRS)

* Request
    * dir( String name ) - The full name of the directory to create in HDFS.
    * perm( String perm ) - The permissions for the directory (e.g. 644).  Optional: default="777"
* Response
    * EmptyResponse - Implicit close().
* Example
    * `Hdfs.mkdir( session ).dir( "/user/guest/example" ).now()`

###### put() - Write a file into HDFS (CREATE)

* Request
    * text( String text ) - Text to upload to HDFS.  Takes precidence over file if both present.
    * file( String name ) - The name of a local file to upload to HDFS.
    * to( String name ) - The fully qualified name to create in HDFS.
* Response
    * EmptyResponse - Implicit close().
* Example
    * `Hdfs.put( session ).file( README ).to( "/user/guest/example/README" ).now()`

###### rm() - Delete a file or directory (DELETE)

* Request
    * file( String name ) - The fully qualified file or directory name in HDFS.
    * recursive( Boolean recursive ) - Delete directory and all of its contents if True.  Optional: default=False
* Response
    * BasicResponse - Implicit close().
* Example
    * `Hdfs.rm( session ).file( "/user/guest/example" ).recursive().now()`





