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

{{Getting Started}}
-------------------

### 2. Enter the `{GATEWAY_HOME}` directory

    cd knox-{VERSION}

The fully qualified name of this directory will be referenced as {{\{GATEWAY_HOME\}}} throughout the remainder of this document.

### 3. Start the demo LDAP server (ApacheDS)

First, understand that the LDAP server provided here is for demonstration purposes.
You may configure the LDAP specifics within the topology descriptor for the cluster as described in step 5 below, in order to customize what LDAP instance to use.
The assumption is that most users will leverage the demo LDAP server while evaluating this release and should therefore continue with the instructions here in step 3.

Edit {{\{GATEWAY_HOME\}/conf/users.ldif}} if required and add your users and groups to the file.
A sample end user "bob" has been already included.
Note that the passwords in this file are "fictitious" and have nothing to do with the actual accounts on the Hadoop cluster you are using.
There is also a copy of this file in the templates directory that you can use to start over if necessary.

Start the LDAP server - pointing it to the config dir where it will find the users.ldif file in the conf directory.

    java -jar bin/ldap.jar conf &

There are a number of log messages of the form {{Created null.}} that can safely be ignored.  Take note of the port on which it was started as this needs to match later configuration.

### 4. Start the Gateway server

    java -jar bin/server.jar

Take note of the port identified in the logging output as you will need this for accessing the gateway.

The server will prompt you for the master secret (password). This secret is used to secure artifacts used to secure artifacts used by the gateway server for things like SSL, credential/password aliasing. This secret will have to be entered at startup unless you choose to persist it. Remember this secret and keep it safe.  It represents the keys to the kingdom. See the Persisting the Master section for more information.

### 5. Configure the Gateway with the topology of your Hadoop cluster

Edit the file {{\{GATEWAY_HOME\}/deployments/sample.xml}}

Change the host and port in the urls of the {{<service>}} elements for NAMENODE, TEMPLETON and OOZIE services to match your Hadoop cluster
deployment.

The default configuration contains the LDAP URL for a LDAP server.  By default that file is configured to access the demo ApacheDS based LDAP
server and its default configuration. By default, this server listens on port 33389.  Optionally, you can change the LDAP URL for the LDAP server to be used for authentication.  This is set via the main.ldapRealm.contextFactory.url property in the {{<gateway><provider><authentication>}} section.

Save the file.  The directory {{\{GATEWAY_HOME\}/deployments}} is monitored by the Gateway server and reacts to the discovery of a new or changed cluster topology descriptor by provisioning the endpoints and required filter chains to serve the needs of each cluster as described by the topology file.  Note that the name of the file excluding the extension is also used as the path for that cluster in the URL.  So for example
the sample.xml file will result in Gateway URLs of the form {{\[http://\]}}{{{}{gateway-host\}:\{gateway-port\}/gateway/sample/namenode/api/v1}}

### 6. Test the installation and configuration of your Gateway

Invoke the LISTSATUS operation on HDFS represented by your configured NAMENODE by using your web browser or curl:

    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sample/namenode/api/v1/?op=LISTSTATUS'

The results of the above command should result in something to along the lines of the output below.  The exact information returned is subject to the content within HDFS in your Hadoop cluster.

    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 760
    Server: Jetty(6.1.26)

    {"FileStatuses":{"FileStatus":[
    {"accessTime":0,"blockSize":0,"group":"hdfs","length":0,"modificationTime":1350595859762,"owner":"hdfs","pathSuffix":"apps","permission":"755","replication":0,"type":"DIRECTORY"},
    {"accessTime":0,"blockSize":0,"group":"mapred","length":0,"modificationTime":1350595874024,"owner":"mapred","pathSuffix":"mapred","permission":"755","replication":0,"type":"DIRECTORY"},
    {"accessTime":0,"blockSize":0,"group":"hdfs","length":0,"modificationTime":1350596040075,"owner":"hdfs","pathSuffix":"tmp","permission":"777","replication":0,"type":"DIRECTORY"},
    {"accessTime":0,"blockSize":0,"group":"hdfs","length":0,"modificationTime":1350595857178,"owner":"hdfs","pathSuffix":"user","permission":"755","replication":0,"type":"DIRECTORY"}
    ]}}

For additional information on WebHDFS, Templeton/WebHCat and Oozie REST APIs, see the following URLs respectively:

* WebHDFS - [http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/WebHDFS.html]
* Templeton/WebHCat - [http://people.apache.org/~thejas/templeton_doc_v1/]
* Oozie - [http://oozie.apache.org/docs/3.3.1/WebServicesAPI.html]


### Examples

More examples can be found [here|Examples].


###. Persisting the Master Secret

The master secret is required to start the server.
This secret is used to access secured artifacts by the gateway instance.
Keystore, trust stores and credential stores are all protected with the master secret.

You may persist the master secret by supplying the *\-persist-master* switch at startup.
This will result in a warning indicating that persisting the secret is less secure than providing it at startup.
We do make some provisions in order to protect the persisted password.

It is encrypted with AES 128 bit encryption and where possible the file permissions are set to only be accessible by the user that the gateway is running as.

After persisting the secret, ensure that the file at config/security/master has the appropriate permissions set for your environment.
This is probably the most important layer of defense for master secret.
Do not assume that the encryption if sufficient protection.

A specific user should be created to run the gateway this will protect a persisted master file.


### Mapping Gateway URLs to Hadoop cluster URLs

The Gateway functions much like a reverse proxy.
As such it maintains a mapping of URLs that are exposed externally by the Gateway to URLs that are provided by the Hadoop cluster.
Examples of mappings for the NameNode and Templeton are shown below.
These mapping are generated from the combination of the Gateway configuration file (i.e. {{\{GATEWAY_HOME\}/conf/gateway-site.xml}}) and the cluster topology descriptors (e.g. {{\{GATEWAY_HOME\}/deployments/\{cluster-name\}.xml}}).

* HDFS (NameNode)
    * Gateway: {nolink:http://\{gateway-host\}:\{gateway-port\}/\{gateway-path\}/\{cluster-name\}/namenode/api/v1}
    * Cluster: {nolink:http://\{namenode-host\}:50070/webhdfs/v1}
* WebHCat (Templeton)
    * Gateway: {nolink:http://\{gateway-host\}:\{gateway-port\}/\{gateway-path\}/\{cluster-name\}/templeton/api/v1}
    * Cluster: {nolink:http://\{templeton-host\}:50111/templeton/v1}
* Oozie
    * Gateway: {nolink:http://\{gateway-host\}:\{gateway-port\}/\{gateway-path\}/\{cluster-name\}/oozie/api/v1}
    * Cluster: {nolink:http://\{templeton-host\}:11000/oozie/v1}

The values for {{\{gateway-host\}}}, {{\{gateway-port\}}}, {{\{gateway-path\}}} are provided via the Gateway configuration file (i.e. `{GATEWAY_HOME\}/conf/gateway-site.xml`).

The value for {{\{cluster-name\}}} is derived from the name of the cluster topology descriptor (e.g. {{\{GATEWAY_HOME\}/deployments/\{cluster-name\}.xml}}).

The value for {{\{namenode-host\}}} and {{\{templeton-host\}}} is provided via the cluster topology descriptor (e.g. {{\{GATEWAY_HOME\}/deployments/\{cluster-name\}.xml}}).

Note: The ports 50070, 50111 and 11000 are the defaults for NameNode, Templeton and Oozie respectively.
Their values can also be provided via the cluster topology descriptor if your Hadoop cluster uses different ports.
