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

An an existing Hadoop 1.x or 2.x cluster is required for Knox sit in front of and protect.
One of the easiest ways to ensure this it to utilize a Hortonworks Sandbox VM.
It is possible to use a Hadoop cluster deployed on EC2 but this will require additional configuration not covered here.
It is also possible to use a limited set of services in Hadoop cluster secured with Kerberos.
This too required additional configuration that is not described here.
See #[Supported Services] for details on what is supported for this release.

The Hadoop cluster should be ensured to have at least WebHDFS, WebHCat (i.e. Templeton) and Oozie configured, deployed and running.
HBase/Stargate and Hive can also be accessed via the Knox Gateway given the proper versions and configuration.

The instructions that follow assume a few things:

1. The gateway is *not* collocated with the Hadoop clusters themselves.
2. The host names and IP addresses of the cluster services are accessible by the gateway where ever it happens to be running.

All of the instructions and samples provided here are tailored and tested to work "out of the box" against a [Hortonworks Sandbox 2.x VM][sandbox].


### Download ###

Download one of the distributions below from the [Apache mirrors][mirror].

* Source archive: [knox-incubating-0.3.0-src.zip][src-zip] ([PGP signature][src-pgp], [SHA1 digest][src-sha], [MD5 digest][src-md5])
* Binary archive: [knox-incubating-0.3.0.zip][bin-zip] ([PGP signature][bin-pgp], [SHA1 digest][bin-sha], [MD5 digest][bin-md5])
* RPM package: [knox-incubating-0.3.0.rpm][rpm] ([PGP signature][rpm-pgp], [SHA1 digest][rpm-sha], [MD5 digest][rpm-md5])

[src-zip]: http://www.apache.org/dyn/closer.cgi/incubator/knox/0.3.0/knox-incubating-0.3.0-src.zip
[src-sha]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0-src.zip.sha
[src-pgp]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-0.3.0-incubating-src.zip.asc
[src-md5]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0-src.zip.md5
[bin-zip]: http://www.apache.org/dyn/closer.cgi/incubator/knox/0.3.0/knox-incubating-0.3.0.zip
[bin-pgp]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.zip.asc
[bin-sha]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.zip.sha
[bin-md5]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.zip.md5
[rpm]: http://www.apache.org/dyn/closer.cgi/incubator/knox/0.3.0/knox-incubating-0.3.0.rpm
[rpm-sha]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.rpm.sha
[rpm-pgp]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-0.3.0-incubating.rpm.asc
[rpm-md5]: http://www.apache.org/dist/incubator/knox/0.3.0/knox-incubating-0.3.0.rpm.md5

Apache Knox Gateway releases are available under the [Apache License, Version 2.0][asl].
See the NOTICE file contained in each release artifact for applicable copyright attribution notices.


### Verify ###

It is essential that you verify the integrity of any downloaded files using the PGP signatures.
Please read [Verifying Apache HTTP Server Releases](http://httpd.apache.org/dev/verification.html) for more information on why you should verify our releases.

The PGP signatures can be verified using PGP or GPG.
First download the KEYS file as well as the .asc signature files for the relevant release packages.
Make sure you get these files from the main distribution directory linked above, rather than from a mirror.
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

The steps required to install the gateway will vary depending upon which distribution format was downloaded.
In either case you will end up with a directory where the gateway is installed.
This directory will be referred to as your `{GATEWAY_HOME}` throughout this document.

#### ZIP ####

If you downloaded the Zip distribution you can simply extract the contents into a directory.
The example below provides a command that can be executed to do this.
Note the `{VERSION}` portion of the command must be replaced with an actual Apache Knox Gateway version number.
This might be 0.3.0 for example and must patch the value in the file downloaded.

    jar xf knox-incubating-{VERSION}.zip

This will create a directory `knox-incubating-{VERSION}` in your current directory.
The directory `knox-incubating-{VERSION}` will considered your `{GATEWAY_HOME}`


#### RPM ####

If you downloaded the RPM distribution you can install it using normal RPM package tools.
It is important that the user that will be running the gateway server is used to install.
This is because several directories are created that are owned by this user.

    sudo yum localinstall knox-incubating-{VERSION}.rpm

or

    sudo rpm -ihv knox-incubating-{VERSION}.rpm


This will create several directories.

    /usr/lib/knox
    /var/log/knox
    /var/run/knox

The directory `/usr/lib/knox` is considered your `{GATEWAY_HOME}` and will adhere to the layout described below.
The directory `/var/log/knox` will contain the output files from the server.
The directory `/var/run/knox` will contain the process ID for a currently running gateway server.

#### Layout ####

Regardless of the installation method used the layout and content of the `{GATEWAY_HOME}` will be identical.
The table below provides a brief explanation of the important files and directories within `{GATEWWAY_HOME}`

| Directory     | Purpose |
| ------------- | ------- |
| conf/         | Contains configuration files that apply to the gateway globally (i.e. not cluster specific ).       |
| bin/          | Contains the executable shell scripts, batch files and JARs for clients and servers.                |
| deployments/  | Contains topology descriptors used to configure the gateway for specific Hadoop clusters.           |
| lib/          | Contains the JARs for all the components that make up the gateway.                                  |
| dep/          | Contains the JARs for all of the components upon which the gateway depends.                         |
| ext/          | A directory where user supplied extension JARs can be placed to extends the gateways functionality. |
| samples/      | Contains a number of samples that can be used to explore the functionality of the gateway.          |
| templates/    | Contains default configuration files that can be copied and customized.                             |
| README        | Provides basic information about the Apache Knox Gateway.                                           |
| ISSUES        | Describes significant know issues.                                                                  |
| CHANGES       | Enumerates the changes between releases.                                                            |
| LICENSE       | Documents the license under which this software is provided.                                        |
| NOTICE        | Documents required attribution notices for included dependencies.                                   |
| DISCLAIMER    | Documents that this release is from a project undergoing incubation at Apache.                      |


### Supported Services ###

This table enumerates the versions of various Hadoop services that have been tested to work with the Knox Gateway.
Only more recent versions of some Hadoop components when secured via Kerberos can be accessed via the Knox Gateway.

| Service            | Version    | Non-Secure  | Secure |
| ------------------ | ---------- | ----------- | ------ |
| WebHDFS            | 2.1.0      | ![y]        | ![y]   |
| WebHCat/Templeton  | 0.11.0     | ![y]        | ![y]   |
| Ozzie              | 4.0.0      | ![y]        | ![y]   |
| HBase/Stargate     | 0.95.2     | ![y]        | ![n]   |
| Hive (via WebHCat) | 0.11.0     | ![y]        | ![n]   |
|                    | 0.12.0     | ![y]        | ![?]   |
| Hive (via JDBC)    | 0.11.0     | ![n]        | ![n]   |
|                    | 0.12.0     | ![?]        | ![?]   |
| Hive (via ODBC)    | 0.11.0     | ![n]        | ![n]   |
| Hive               | 0.12.0     | ![?]        | ![?]   |


### Sandbox Configuration ###

TODO


### Basic Usage ###

The steps described below are intended to get the Knox Gateway server up and running in its default configuration.
Once that is accomplished a very simple example of using the gateway to interact with a Hadoop cluster is provided.
More detailed configuration information is provided in the #[Gateway Details] section.
More detailed examples for using each Hadoop service can be found in the #[Service Details] section.

Note that *nix conventions are used throughout this section but in general the Windows alternative should be obvious.
In situations where this is not the case a Windows alternative will be provided.

#### Starting Servers ####

##### 1. Enter the `{GATEWAY_HOME}` directory

    cd knox-incubation-{VERSION}

The fully qualified name of this directory will be referenced as `{GATEWAY_HOME}` throughout this document.

##### 2. Start the demo LDAP server (ApacheDS)

First, understand that the LDAP server provided here is for demonstration purposes.
You may configure the gateway to utilize other LDAP systems via the topology descriptor.
This is described in step 5 below.
The assumption is that most users will leverage the demo LDAP server while evaluating this release and should therefore continue with the instructions here in step 3.

Edit `{GATEWAY_HOME}/conf/users.ldif` if required and add any desired users and groups to the file.
A sample end user "guest" has been already included.
Note that the passwords in this file are "fictitious" and have nothing to do with the actual accounts on the Hadoop cluster you are using.
There is also a copy of this file in the templates directory that you can use to start over if necessary.
This file is only used by the demo LDAP server.

Start the LDAP server - pointing it to the config dir where it will find the `users.ldif` file in the conf directory.

    java -jar bin/ldap.jar conf &

_On windows this command can be run in its own command window instead of running it in the background via `&`._

There are a number of log messages of the form `Created null.` that can safely be ignored.
Take note of the port on which it was started as this needs to match later configuration.

##### 3. Start the gateway server

The gateway can be started in one of two ways depending upon your preferences.
The first way is to start the gateway directly using the Java's java -jar command line.
The script bin/gateway.sh can also be used to start the gateway.
Both options are detailed below.

###### Starting via Java

This is the simplest way to start the gateway.
Starting this way will result in all logging being written directly to standard output.

    java -jar bin/gateway.jar

The server will prompt you for the master secret (i.e. password).
This secret is used to secure artifacts used by the gateway server for things like SSL and credential/password aliasing.
This secret will have to be entered at startup unless you choose to persist it.
See the Persisting the Master section for more information.
Remember this secret and keep it safe.
It represents the keys to the kingdom.

Take note of the port identified in the logging output as you will need this for accessing the gateway.

###### Starting via script (*nix only)

Starting the gateway using the script is a bit more in line with how other Hadoop components are started.
Before actually starting the server this way a setup step needs to be performed.
This step is required because directories are created in /var/log and /var/run if required.
These directories can only be created by the root user so the setup command must be run with root privileges.

    sudo bin/gateway.sh setup

The server will prompt you for the master secret (i.e. password).
This secret is used to secure artifacts used by the gateway server for things like SSL and credential/password aliasing.
This secret will have to be entered at startup unless you choose to persist it.
See the Persisting the Master section for more information.
Remember this secret and keep it safe.
It represents the keys to the kingdom.

The server can then be started without root privileges using this command.

    bin/gateway.sh start

When starting the gateway this way the process will be run in the backgroud.
The log output is written into the directory /var/log/knox.
In addition a PID (process ID) is written into /var/run/knox.

In order to stop a gateway that was started with the script use this command.

    bin/gateway.sh stop

If for some reason the gateway is stopped other than by using the command above you may need to clear the tracking PID.

    bin/gateway.sh clean

__CAUTION: This command will also clear any log output in /var/log/knox so use this with caution.__

##### 4. Configure the Gateway with the topology of your Hadoop cluster

Edit the file `{GATEWAY_HOME}/deployments/sandbox.xml`

Change the host and port in the urls of the `<service>` elements for WEBHDFS, WEBHCAT, OOZIE, WEBHBASE and HIVE services to match your Hadoop cluster deployment.

The default configuration contains the LDAP URL for a LDAP server.
By default that file is configured to access the demo ApacheDS based LDAP server and its default configuration.
The ApacheDS based LDAP server listens on port 33389 by default.
Optionally, you can change the LDAP URL for the LDAP server to be used for authentication.
This is set via the `main.ldapRealm.contextFactory.url` property in the `<gateway><provider><authentication>` section.
If you use an LDAP system other than the demo LDAP server you may need to change additional configuration as well.

Save the file.
The directory `{GATEWAY_HOME}/deployments` is monitored by the gateway server.
When a new or changed cluster topology descriptor is detected, it will provision the endpoints for the services described in the topology descriptor.
Note that the name of the file excluding the extension is also used as the path for that cluster in the URL.
For example the `sandbox.xml` file will result in gateway URLs of the form `http://{gateway-host}:{gateway-port}/gateway/sandbox/webhdfs`.

##### 5. Test the installation

Invoke the LISTSATUS operation on WebHDFS via the gateway.
This will return a directory listing of the root (i.e. /) directory of HDFS.

    curl -i -k -u guest:guest-password -X GET \
        'https://localhost:8443/gateway/sandbox/webhdfs/v1/?op=LISTSTATUS'

The results of the above command should result in something to along the lines of the output below.
The exact information returned is subject to the content within HDFS in your Hadoop cluster.
Successfully executing this command at a minimum proves that the gateway is properly configured to provide access to WebHDFS.
It does not necessarily provide that any of the other services are correct configured to be accessible.
To validate that see the sections for the individual services in #[Service Details].

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

For additional information on WebHDFS, WebHCat/Templeton, Oozie and HBase/Stargate REST APIs, see the following URLs respectively:

* WebHDFS - http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/WebHDFS.html
* WebHCat (Templeton) - http://people.apache.org/~thejas/templeton_doc_v1
* Oozie - http://oozie.apache.org/docs/3.3.1/WebServicesAPI.html
* Stargate (HBase) - http://wiki.apache.org/hadoop/Hbase/Stargate

### More Examples ###

These examples provide more detail about how to access various Apache Hadoop services via the Apache Knox Gateway.

* #[WebHDFS Examples]
* #[WebHCat Examples]
* #[Oozie Examples]
* #[HBase Examples]
* #[Hive Examples]
