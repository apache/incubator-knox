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
* [Usage Examples](#Usage+Examples)
* [Gateway Details](#Gateway+Details)
    * [Authentication](#Authentication)
    * [Authorization](#Authorization)
* [Service Details](#Service+Details)
    * [WebHDFS](#WebHDFS)
    * [WebHCat/Templeton](#WebHCat)
    * [Oozie](#Oozie)
    * [HBase/Starbase](#HBase)
    * [Hive](#Hive)
* [Secure Clusters](#Secure+Clusters)
* [Trouble Shooting](#Trouble+Shooting)
* [Export Controls](#Export+Controls)
* [Release Verification](#Release+Verification)


{{Introduction}}
------------------------------

TODO


{{Download}}
------------

TODO

| ![$] Important |
| -------------- |
| Please ensure that you validate the integrity of any downloaded files as described [below](#Release+Verification). |

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


{{Installation}}
----------------

### ZIP ###

TODO


### RPM ###

TODO


### Layout ###

TODO - Describe the purpose of all of the directories


{{Getting Started}}
-------------------

TODO


{{Supported Services}}
----------------------

This table enumerates the versions of various Hadoop services that have been tested to work with the Knox Gateway.
Only more recent versions of some Hadoop components when secured via Kerberos can be accessed via the Knox Gateway.

| Service           | Version    | Non-Secure  | Secure |
| ----------------- | ---------- | ----------- | ------ |
| WebHDFS           | 2.1.0      | ![y]        | ![y]   |
| WebHCat/Templeton | 0.11.0     | ![y]        | ![n]   |
| Ozzie             | 4.0.0      | ![y]        | ![?]   |
| HBase/Stargate    | 0.95.2     | ![y]        | ![?]   |
| Hive/JDBC         | 0.11.0     | ![n]        | ![n]   |
|                   | 0.12.0     | ![?]![y]    | ![?]   |
| Hive/ODBC         | 0.12.0     | ![?]        | ![?]   |


{{Usage Examples}}
------------------

These examples provide more detail about how to access various Apache Hadoop services via the Apache Knox Gateway.

* [WebHDFS](#WebHDFS+Examples)
* [WebHCat/Templeton](#WebHCat+Examples)
* [Oozie](#Oozie+Examples)
* [HBase](#HBase+Examples)
* [Hive](#Hive+Examples)


{{Gateway Details}}
-------------------

TODO

<<authn.md>>
<<authz.md>>


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

<<../common/footer.md>>

