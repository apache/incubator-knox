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

## Quick Start ##

Here are the steps to have Apache Knox up and running against a Hadoop Cluster:

1. Verify system requirements
1. Download a virtual machine (VM) with Hadoop 
1. Download Apache Knox Gateway
1. Start the virtual machine with Hadoop
1. Install Knox
1. Start the LDAP embedded within Knox
1. Start the Knox Gateway
1. Do Hadoop with Knox



### 1 - Requirements ###

#### Java ####

Java 1.6 or later is required for the Knox Gateway runtime.
Use the command below to check the version of Java installed on the system where Knox will be running.

    java -version

#### Hadoop ####

Knox supports Hadoop 1.x or 2.x, the quick start instructions assume a Hadoop 2.x virtual machine based environment. 


### 2 - Download Hadoop 2.x VM ###
The quick start provides a link to download Hadoop 2.0 based Hortonworks virtual machine [Sandbox](http://hortonworks.com/products/hdp-2/#install). Please note Knox supports other Hadoop distributions and is configurable against a full blown Hadoop cluster.
Configuring Knox for Hadoop 1.x/2.x version, or Hadoop deployed in EC2 or a custom Hadoop cluster is documented in advance deployment guide.


### 3 - Download Apache Knox Gateway ###

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

While recommended, verify is an optional step. You can verify the integrity of any downloaded files using the PGP signatures.
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

### 4 - Start Hadoop virtual machine ###

Start the Hadoop virtual machine.

### 5 - Install Knox ###

The steps required to install the gateway will vary depending upon which distribution format (zip | rpm) was downloaded.
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
These command will install Knox to `/usr/lib/knox` following the pattern of other Hadoop components.
This directory will be considered your `{GATEWAY_HOME}`.

    sudo yum localinstall knox-incubating-{VERSION}.rpm

or

    sudo rpm -ihv knox-incubating-{VERSION}.rpm


### 6 - Start LDAP embedded in Knox ###

Knox comes with an LDAP server for demonstration purposes.

    java -jar {GATEWAY_HOME}/bin/ldap.jar conf &


### 7 - Start Knox  ###

The gateway can be started in one of two ways, as java -jar or with a shell script.


###### Starting via Java

This is the simplest way to start the gateway.
Starting this way will result in all logging being written directly to standard output.

    java -jar {GATEWAY_HOME}/bin/gateway.jar


Upon start, Knox server will prompt you for the master secret (i.e. password).
This secret is used to secure artifacts used by the gateway server for things like SSL and credential/password aliasing.
This secret will have to be entered at startup unless you choose to persist it.


###### Starting via script (*nix only)

Run the setup command with root privileges.

    sudo {GATEWAY_HOME}/bin/gateway.sh setup

The server will prompt you for the master secret (i.e. password).

The server can then be started without root privileges using this command.

    {GATEWAY_HOME}/bin/gateway.sh start

When starting the gateway this way the process will be run in the backgroud.
The log output is written into the directory /var/log/knox.
In addition a PID (process ID) is written into /var/run/knox.

In order to stop a gateway that was started with the script use this command.

    {GATEWAY_HOME}/bin/gateway.sh stop

If for some reason the gateway is stopped other than by using the command above you may need to clear the tracking PID.

    {GATEWAY_HOME}/bin/gateway.sh clean

__NOTE: This command will also clear any log output in /var/log/knox so use this with caution.__


### 8 - Do Hadoop with Knox

#### Put a file in HDFS via Knox.
#### CAT a file in HDFS via Knox.
#### Invoke the LISTSATUS operation on WebHDFS via the gateway.
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
    
#### Submit a MR job via Knox.

#### Get status of a MR job via Knox.

#### Cancel a MR job via Knox.


### More Examples ###

