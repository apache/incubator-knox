<!--
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
-->

<<../common/header.md>>

Apache Knox Gateway 0.3.0 (Incubator)
=====================================


Table Of Contents
-----------------

* [Introduction](#Introduction)
* [Download](#Download)
* [Installation](#Installation)
* [Getting Started](#Getting+Started)
* [Supported Services](#Supported+Services)
* [Sandbox Configuration](#Sandbox+Configuration)
* [Usage Examples](#Usage+Examples)
* [Gateway Details](#Gateway+Details)
    * [Authentication](#Authentication)
    * [Authorization](#Authorization)
    * [Configuration](#Configuration)
* [Client Details](#Client+Details)
* [Service Details](#Service+Details)
    * [WebHDFS](#WebHDFS)
    * [WebHCat/Templeton](#WebHCat)
    * [Oozie](#Oozie)
    * [HBase/Starbase](#HBase)
    * [Hive](#Hive)
* [Secure Clusters](#Secure+Clusters)
* [Trouble Shooting](#Trouble+Shooting)
* [Release Verification](#Release+Verification)
* [Export Controls](#Export+Controls)


{{Introduction}}
------------------------------

TODO


{{Requirements}}
----------------

### Java ###

Java 1.6 or later is required for the Knox Gateway runtime.
Use the command below to check the version of Java installed on the system where Knox will be running.

    java -version

### Hadoop ###

An an existing Hadoop 1.x or 2.x cluster is required for Knox to protect.
One of the easiest ways to ensure this it to utilize a HDP Sandbox VM.
It is possible to use a Hadoop cluster deployed on EC2 but this will require additional configuration.
Currently if this Hadoop cluster is secured with Kerberos only WebHDFS will work and additional configuration is required.

The Hadoop cluster should be ensured to have at least WebHDFS, WebHCat (i.e. Templeton) and Oozie configured, deployed and running.
HBase/Stargate and Hive can also be accessed via the Knox Gateway given the proper versions and configuration.

The instructions that follow assume that the Gateway is *not* collocated with the Hadoop clusters themselves and (most importantly) that the hostnames and IP addresses of the cluster services are accessible by the gateway where ever it happens to be running.
All of the instructions and samples are tailored to work "out of the box" against a Hortonworks Sandbox 2.x VM.

This release of the Apache Knox Gateway has been tested against the [Hortonworks Sandbox 2.0](http://hortonworks.com/products/hortonworks-sandbox/).


{{Download}}
------------

Download and extract the knox-\{VERSION\}.zip}} file into the installation directory that will contain your {{\{GATEWAY_HOME\}}}.
You can find the downloads for Knox releases on the [Apache mirrors|http://www.apache.org/dyn/closer.cgi/incubator/knox/].

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

| ![$] Important |
| -------------- |
| Please ensure that you validate the integrity of any downloaded files as described [below](#Release+Verification). |

Apache Knox Gateway releases are available under the [Apache License, Version 2.0][asl].
See the NOTICE file contained in each release artifact for applicable copyright attribution notices.


<<install.md>>
<<using.md>>


{{Supported Services}}
----------------------

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


<<sandbox.md>>


{{Usage Examples}}
------------------

These examples provide more detail about how to access various Apache Hadoop services via the Apache Knox Gateway.

* [WebHDFS](#WebHDFS+Examples)
* [WebHCat/Templeton](#WebHCat+Examples)
* [Oozie](#Oozie+Examples)
* [HBase](#HBase+Examples)
* [Hive](#Hive+Examples)

<<config.md>>

{{Gateway Details}}
-------------------

TODO

<<config.md>>
<<authn.md>>
<<authz.md>>
<<client.md>>

{{Service Details}}
-------------------

TODO

<<webhdfs.md>>
<<webhcat.md>>
<<oozie.md>>
<<hbase.md>>
<<hive.md>>
<<kerberos.md>>
<<trouble.md>>


{{Release Verification}}
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


{{Export Controls}}
-------------------

Apache Knox Gateway includes cryptographic software.
The country in which you currently reside may have restrictions on the import, possession, use, and/or re-export to another country, of encryption software.
BEFORE using any encryption software, please check your country's laws, regulations and policies concerning the import, possession, or use, and re-export of encryption software, to see if this is permitted.
See http://www.wassenaar.org for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS), has classified this software as Export Commodity Control Number (ECCN) 5D002.C.1, which includes information security software using or performing cryptographic functions with asymmetric algorithms.
The form and manner of this Apache Software Foundation distribution makes it eligible for export under the License Exception ENC Technology Software Unrestricted (TSU) exception (see the BIS Export Administration Regulations, Section 740.13) for both object code and source code.

The following provides more details on the included cryptographic software:

* Apache Knox Gateway uses the ApacheDS which in turn uses Bouncy Castle generic encryption libraries.
* See http://www.bouncycastle.org for more details on Bouncy Castle.
* See http://directory.apache.org/apacheds for more details on ApacheDS.


<<../common/footer.md>>

