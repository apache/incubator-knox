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

## Apache Knox Details ##

This section provides everything you need to know to get the Knox gateway up and running against a Hadoop cluster.

#### Hadoop ####

An an existing Hadoop 1.x or 2.x cluster is required for Knox sit in front of and protect.
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


#### Apache Knox Directory Layout ####

Knox can be installed by expanding the zip file or with rpm. With rpm based install the following directories are created in addition to those described in
this section.

    /usr/lib/knox
    /var/log/knox
    /var/run/knox

The directory `/usr/lib/knox` is considered your `{GATEWAY_HOME}` and will adhere to the layout described below.
The directory `/var/log/knox` will contain the output files from the server.
The directory `/var/run/knox` will contain the process ID for a currently running gateway server.


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
| WebHCat/Templeton  | 0.11.0     | ![y]        | ![n]   |
|                    | 0.12.0     | ![y]        | ![y]   |
| Ozzie              | 4.0.0      | ![y]        | ![y]   |
| HBase/Stargate     | 0.95.2     | ![y]        | ![n]   |
| Hive (via WebHCat) | 0.11.0     | ![y]        | ![n]   |
|                    | 0.12.0     | ![y]        | ![y]   |
| Hive (via JDBC)    | 0.11.0     | ![n]        | ![n]   |
|                    | 0.12.0     | ![y]        | ![n]   |
| Hive (via ODBC)    | 0.11.0     | ![n]        | ![n]   |
|                    | 0.12.0     | ![n]        | ![n]   |


### Sandbox Configuration ###

TODO

### More Examples ###

These examples provide more detail about how to access various Apache Hadoop services via the Apache Knox Gateway.

* #[WebHDFS Examples]
* #[WebHCat Examples]
* #[Oozie Examples]
* #[HBase Examples]
* #[Hive Examples]
