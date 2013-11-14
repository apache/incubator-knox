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

## Troubleshooting ##

### Finding Logs ###

When things aren't working the first thing you need to do is examine the diagnostic logs.
Depending upon how you are running the gateway these diagnostic logs will be output to different locations.

#### java -jar bin/gateway.jar ####

When the gateway is run this way the diagnostic output is written directly to the console.
If you want to capture that output you will need to redirect the console output to a file using OS specific techniques.

    java -jar bin/gateway.jar > gateway.log

#### bin/gateway.sh start ####

When the gateway is run this way the diagnostic output is written to /var/log/knox/knox.out and /var/log/knox/knox.err.
Typically only knox.out will have content.


### Increasing Logging ###

The `log4j.properties` files `{GATEWAY_HOME}/conf` can be used to change the granularity of the logging done by Knox.
The Knox server must be restarted in order for these changes to take effect.
There are various useful loggers pre-populated but commented out.

    log4j.logger.org.apache.hadoop.gateway=DEBUG # Use this logger to increase the debugging of Apache Knox itself.
    log4j.logger.org.apache.shiro=DEBUG          # Use this logger to increase the debugging of Apache Shiro.
    log4j.logger.org.apache.http=DEBUG           # Use this logger to increase the debugging of Apache HTTP components.
    log4j.logger.org.apache.http.client=DEBUG    # Use this logger to increase the debugging of Apache HTTP client component.
    log4j.logger.org.apache.http.headers=DEBUG   # Use this logger to increase the debugging of Apache HTTP header.
    log4j.logger.org.apache.http.wire=DEBUG      # Use this logger to increase the debugging of Apache HTTP wire traffic.


### LDAP Server Connectivity Issues ###

If the gateway cannot contact the configured LDAP server you will see errors in the gateway diagnostic output.

    TODO:Kevin - What does it look like when the LDAP server isn't running.

Resolving this will require ensuring that the LDAP server is running and that connection information is correct.
The LDAP server connection information is configured in the cluster's topology file (e.g. {GATEWAY_HOME}/deployments/sandbox.xml).


### Hadoop Cluster Connectivity Issues ###

If the gateway cannot contact one of the services in the configured Hadoop cluster you will see errors in the gateway diagnostic output.

    TODO:Kevin - What does it look like when the Sandbox isn't running.

Resolving this will require ensuring that the Hadoop services are running and that connection information is correct.
Basic Hadoop connectivity can be evaluated using cURL as described elsewhere.
Otherwise the Hadoop cluster connection information is configured in the cluster's topology file (e.g. {GATEWAY_HOME}/deployments/sandbox.xml).


### Check Hadoop Cluster Access via cURL ###

When you are experiencing connectivity issue it can be helpful to "bypass" the gateway and invoke the Hadoop REST APIs directly.
This can easily be done using the cURL command line utility or many other REST/HTTP clients.
Exactly how to use cURL depends on the configuration of your Hadoop cluster.
In general however you will use a command line the one that follows.

    curl -ikv -X GET 'http://namenode-host:50070/webhdfs/v1/?op=LISTSTATUS'

If you are using Sandbox the WebHDFS or NameNode port will be mapped to localhost so this command can be used.

    curl -ikv -X GET 'http://localhost:50070/webhdfs/v1/?op=LISTSTATUS'

If you are using a cluster secured with Kerberos you will need to have used `kinit` to authenticate to the KDC.
Then the command below should verify that WebHDFS in the Hadoop cluster is accessible.

    curl -ikv --negotiate -u : -X 'http://localhost:50070/webhdfs/v1/?op=LISTSTATUS'


### Authentication Issues ###

TODO:Kevin - What does it look like when the username/password don't match what is in LDAP?


### Hostname Resolution Issues ###

TODO:Kevin - What does it look like when host mapping is enabled and shouldn't be or vice versa.


### Job Submission Issues - HDFS Home Directories ###

TODO:Dilli - What does it look like if the LDAP authenticated user doesn't have a HDFS home directory and submits a job.


### Job Submission Issues - OS Accounts ###

TODO:Dilli - What does it look like if the LDAP authenticated user submits a job but doesn't have an OS account.


### HBase Issues ###

TODO:Kevin - What does it look like when HBase/Stargate hangs and how do you fix it.


### SSL Certificate Issues ###

TODO:Larry - What does it look like when a client doesn't trust the gateway's SSL identity certificate?


### Filing Bugs ###

Bugs can be filed using [Jira][jira].
Please include the results of this command below in the Environment section.
Also include the version of Hadoop being used in the same section.

    cd {GATEWAY_HOME}
    java -jar bin/gateway.jar -version

