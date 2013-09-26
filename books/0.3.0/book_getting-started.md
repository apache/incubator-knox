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

## Getting Started ##

This section provides everything you need to know to get the gateway up and running against a Sandbox VM Hadoop cluster.


### Requirements ###

#### Java ####

Java 1.6 or later is required for the Knox Gateway runtime.
Use the command below to check the version of Java installed on the system where Knox will be running.

    java -version

#### Hadoop ####

An an existing Hadoop 1.x or 2.x cluster is required for Knox to protect.
One of the easiest ways to ensure this it to utilize a Hortonworks Sandbox VM.
It is possible to use a Hadoop cluster deployed on EC2 but this will require additional configuration not covered here.
It is also possible to use a limited set of services in Hadoop cluster secured with Kerberos.
This too required additional configuration that is not described here.

The Hadoop cluster should be ensured to have at least WebHDFS, WebHCat (i.e. Templeton) and Oozie configured, deployed and running.
HBase/Stargate and Hive can also be accessed via the Knox Gateway given the proper versions and configuration.

The instructions that follow assume a few things:

1. The gateway is *not* collocated with the Hadoop clusters themselves 
2. The host names and IP addresses of the cluster services are accessible by the gateway where ever it happens to be running.

All of the instructions and samples provided here are tailored and tested to work "out of the box" against a [Hortonworks Sandbox 2.x VM][sandbox].


### Download ###

Download and extract the knox-{VERSION}.zip file into the installation directory.
This directory will be referred to as your `{GATEWAY_HOME}`.
You can find the downloads for Knox releases on the [Apache mirrors][mirror].

* Source archive: [knox-incubating-0.3.0-src.zip][src-zip] ([PGP signature][src-pgp], [SHA1 digest][src-sha], [MD5 digest][src-md5])
* Binary archive: [knox-incubating-0.3.0.zip][bin-zip] ([PGP signature][bin-pgp], [SHA1 digest][bin-sha], [MD5 digest][bin-md5])

[src-zip]: http://www.apache.org/dyn/closer.cgi/incubator/knox/0.3.0/knox-incubating-0.3.0-src.zip
[src-sha]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0-src.zip.sha
[src-pgp]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-0.3.0-incubating-src.zip.asc
[src-md5]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0-src.zip.md5
[bin-zip]: http://www.apache.org/dyn/closer.cgi/incubator/knox/0.3.0/knox-incubating-0.3.0.zip
[bin-pgp]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.zip.asc
[bin-sha]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.zip.sha
[bin-md5]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.zip.md5

Apache Knox Gateway releases are available under the [Apache License, Version 2.0][asl].
See the NOTICE file contained in each release artifact for applicable copyright attribution notices.


{{Verify}}
------------------------

It is essential that you verify the integrity of the downloaded files using the PGP signatures.
Please read Verifying Apache HTTP Server Releases for more information on why you should verify our releases.

The PGP signatures can be verified using PGP or GPG.
First download the KEYS file as well as the .asc signature files for the relevant release packages.
Make sure you get these files from the main distribution directory, rather than from a mirror.
Then verify the signatures using one of the methods below.

    % pgpk -a KEYS
    % pgpv knox-incubating-0.3.0.zip.asc

or

    % pgp -ka KEYS
    % pgp knox-incubating-0.3.0.zip.asc

or

    % gpg --import KEYS
    % gpg --verify knox-incubating-0.3.0.zip.asc


### Install ###

#### ZIP ####

Download and extract the `knox-{VERSION}.zip` file into the installation directory that will contain your `{GATEWAY_HOME}`.
You can find the downloads for Knox releases on the [Apache mirrors][mirror].

    jar xf knox-{VERSION}.zip

This will create a directory `knox-{VERSION}` in your current directory.


#### RPM ####

TODO


#### Layout ####

TODO - Describe the purpose of all of the directories


### Supported Services ###

This table enumerates the versions of various Hadoop services that have been tested to work with the Knox Gateway.
Only more recent versions of some Hadoop components when secured via Kerberos can be accessed via the Knox Gateway.

| Service           | Version    | Non-Secure  | Secure |
| ----------------- | ---------- | ----------- | ------ |
| WebHDFS           | 2.1.0      | ![y]        | ![?]![y]   |
| WebHCat/Templeton | 0.11.0     | ![y]        | ![?]![n]   |
| Ozzie             | 4.0.0      | ![y]        | ![?]   |
| HBase/Stargate    | 0.95.2     | ![y]        | ![?]   |
| Hive/JDBC         | 0.11.0     | ![n]        | ![n]   |
|                   | 0.12.0     | ![?]![y]    | ![?]   |
| Hive/ODBC         | 0.12.0     | ![?]        | ![?]   |

ProxyUser feature of WebHDFS, WebHCat and Oozie required for secure cluster support seem to work fine.
Knox code seems to be broken for support of secure cluster at this time for WebHDFS, WebHCat and Oozie.


### Basic Usage ###

#### Starting Servers ####

##### 1. Enter the `{GATEWAY_HOME}` directory

    cd knox-{VERSION}

The fully qualified name of this directory will be referenced as `{GATEWAY_HOME}}} throughout the remainder of this document.

##### 2. Start the demo LDAP server (ApacheDS)

First, understand that the LDAP server provided here is for demonstration purposes.
You may configure the LDAP specifics within the topology descriptor for the cluster as described in step 5 below, in order to customize what LDAP instance to use.
The assumption is that most users will leverage the demo LDAP server while evaluating this release and should therefore continue with the instructions here in step 3.

Edit `{GATEWAY_HOME}/conf/users.ldif` if required and add your users and groups to the file.
A sample end user "bob" has been already included.
Note that the passwords in this file are "fictitious" and have nothing to do with the actual accounts on the Hadoop cluster you are using.
There is also a copy of this file in the templates directory that you can use to start over if necessary.

Start the LDAP server - pointing it to the config dir where it will find the users.ldif file in the conf directory.

    java -jar bin/ldap.jar conf &

There are a number of log messages of the form {{Created null.` that can safely be ignored.
Take note of the port on which it was started as this needs to match later configuration.

##### 3. Start the gateway server

    java -jar bin/server.jar

Take note of the port identified in the logging output as you will need this for accessing the gateway.

The server will prompt you for the master secret (password).
This secret is used to secure artifacts used to secure artifacts used by the gateway server for things like SSL, credential/password aliasing.
This secret will have to be entered at startup unless you choose to persist it.
Remember this secret and keep it safe.
It represents the keys to the kingdom. See the Persisting the Master section for more information.

##### 4. Configure the Gateway with the topology of your Hadoop cluster

Edit the file `{GATEWAY_HOME}/deployments/sandbox.xml`

Change the host and port in the urls of the `<service>` elements for WEBHDFS, WEBHCAT, OOZIE, WEBHBASE and HIVE services to match your Hadoop cluster deployment.

The default configuration contains the LDAP URL for a LDAP server.
By default that file is configured to access the demo ApacheDS based LDAP
server and its default configuration. By default, this server listens on port 33389.
Optionally, you can change the LDAP URL for the LDAP server to be used for authentication.
This is set via the main.ldapRealm.contextFactory.url property in the `<gateway><provider><authentication>` section.

Save the file.
The directory `{GATEWAY_HOME}/deployments` is monitored by the gateway server.
When a new or changed cluster topology descriptor is detected, it will provision the endpoints for the services described in the topology descriptor.
Note that the name of the file excluding the extension is also used as the path for that cluster in the URL.
For example the `sandbox.xml` file will result in gateway URLs of the form `http://{gateway-host}:{gateway-port}/gateway/sandbox/webhdfs`.

##### 5. Test the installation and configuration of your Gateway

Invoke the LISTSATUS operation on HDFS represented by your configured NAMENODE by using your web browser or curl:

    curl -i -k -u bob:bob-password -X GET \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/?op=LISTSTATUS'

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

* WebHDFS - http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/WebHDFS.html
* WebHCat (Templeton) - http://people.apache.org/~thejas/templeton_doc_v1
* Oozie - http://oozie.apache.org/docs/3.3.1/WebServicesAPI.html
* Stargate (HBase) - http://wiki.apache.org/hadoop/Hbase/Stargate

### More Examples ###

These examples provide more detail about how to access various Apache Hadoop services via the Apache Knox Gateway.

* [WebHDFS](#WebHDFS+Examples)
* [WebHCat/Templeton](#WebHCat+Examples)
* [Oozie](#Oozie+Examples)
* [HBase](#HBase+Examples)
* [Hive](#Hive+Examples)


{{Sandbox Configuration}}
-------------------------

This version of the Apache Knox Gateway is tested against [Hortonworks Sandbox 2.x|sandbox]

Currently there is an issue with Sandbox that prevents it from being easily used with the gateway.
In order to correct the issue, you can use the commands below to login to the Sandbox VM and modify the configuration.
This assumes that the name sandbox is setup to resolve to the Sandbox VM.
It may be necessary to use the IP address of the Sandbox VM instead.
*This is frequently but not always `192.168.56.101`.*

    ssh root@sandbox
    cp /usr/lib/hadoop/conf/hdfs-site.xml /usr/lib/hadoop/conf/hdfs-site.xml.orig
    sed -e s/localhost/sandbox/ /usr/lib/hadoop/conf/hdfs-site.xml.orig > /usr/lib/hadoop/conf/hdfs-site.xml
    shutdown -r now


In addition to make it very easy to follow along with the samples for the gateway you can configure your local system to resolve the address of the Sandbox by the names `vm` and `sandbox`.
The IP address that is shown below should be that of the Sandbox VM as it is known on your system.
*This will likely, but not always, be `192.168.56.101`.*

On Linux or Macintosh systems add a line like this to the end of the file `/etc/hosts` on your local machine, *not the Sandbox VM*.
_Note: The character between the 192.168.56.101 and vm below is a *tab* character._

    192.168.56.101	vm sandbox

On Windows systems a similar but different mechanism can be used.  On recent
versions of windows the file that should be modified is `%systemroot%\system32\drivers\etc\hosts`



