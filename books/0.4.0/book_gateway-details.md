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

## Gateway Details ##

TODO

### URL Mapping ###

The gateway functions much like a reverse proxy.
As such it maintains a mapping of URLs that are exposed externally by the gateway to URLs that are provided by the Hadoop cluster.
Examples of mappings for the WebHDFS, WebHCat, Oozie and Stargate/HBase are shown below.
These mapping are generated from the combination of the gateway configuration file (i.e. `{GATEWAY_HOME}/conf/gateway-site.xml`) and the cluster topology descriptors (e.g. `{GATEWAY_HOME}/deployments/{cluster-name}.xml`).
The port numbers show for the Cluster URLs represent the default ports for these services.
The actual port number may be different for a given cluster.

* WebHDFS
    * Gateway: `https://{gateway-host}:{gateway-port}/{gateway-path}/{cluster-name}/webhdfs`
    * Cluster: `http://{webhdfs-host}:50070/webhdfs`
* WebHCat (Templeton)
    * Gateway: `https://{gateway-host}:{gateway-port}/{gateway-path}/{cluster-name}/templeton`
    * Cluster: `http://{webhcat-host}:50111/templeton}`
* Oozie
    * Gateway: `https://{gateway-host}:{gateway-port}/{gateway-path}/{cluster-name}/oozie`
    * Cluster: `http://{oozie-host}:11000/oozie}`
* Stargate (HBase)
    * Gateway: `https://{gateway-host}:{gateway-port}/{gateway-path}/{cluster-name}/hbase`
    * Cluster: `http://{hbase-host}:60080`

The values for `{gateway-host}`, `{gateway-port}`, `{gateway-path}` are provided via the gateway configuration file (i.e. `{GATEWAY_HOME}/conf/gateway-site.xml`).

The value for `{cluster-name}` is derived from the file name of the cluster topology descriptor (e.g. `{GATEWAY_HOME}/deployments/{cluster-name}.xml`).

The value for `{webhdfs-host}`, `{webhcat-host}`, `{oozie-host}` and `{hbase-host}` are provided via the cluster topology descriptor (e.g. `{GATEWAY_HOME}/deployments/{cluster-name}.xml`).

Note: The ports 50070, 50111, 11000 and 60080 are the defaults for WebHDFS, WebHCat, Oozie and Stargate/HBase respectively.
Their values can also be provided via the cluster topology descriptor if your Hadoop cluster uses different ports.

<<config.md>>
<<knox_cli.md>>
<<config_authn.md>>
<<config_ldap_group_lookup.md>>
<<config_id_assertion.md>>
<<config_authz.md>>
<<config_kerberos.md>>
<<config_ha.md>>

