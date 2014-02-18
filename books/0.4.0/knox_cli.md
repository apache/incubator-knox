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

### Knox CLI ###
The Knox CLI is a command line utility for management of various aspects of the Knox deployment. It is primarily concerned with the management of the security artifacts for the gateway instance and each of the deployed topologies or hadoop clusters that are gated by the Knox Gateway instance.

The various security artifacts are also generated and populated automatically by the Knox Gateway runtime when they are not found at startup. The assumptions made in those cases are appropriate for a test or development gateway instance and assume 'localhost' for hostname specific activities. For production deployments the use of the CLI may aid in managing  some production deployments.

The knoxcli.sh script is located in the {GATEWAY_HOME}/bin directory.

#### Help ####
##### knoxcli.sh [--help] #####
prints help for all commands

#### Master secret persistence ####
##### knoxcli.sh create-master [--help] #####
Creates and persists an encrypted master secret in a file within {GATEWAY_HOME}/data/security/master. 

NOTE: This command fails when there is an existing master file in the expected location.

#### Alias creation ####
##### knoxcli.sh create-alias n [--cluster c] [--value v] [--generate] [--help] #####
Creates a password alias and stores it in a credential store within the {GATEWAY_HOME}/data/security/keystores dir.  

argument | description
---------|-----------
--name|name of the alias to create  
--cluster|name of Hadoop cluster for the cluster specific credential store otherwise assumes that it is for the gateway itself
--value|parameter for specifying the actual password otherwise prompted<br/>
--generate|boolean flag to indicate whether the tool should just generate the value. This assumes that --value is not set - will result in error otherwise. User will not be prompted for the value when --generate is set.		

#### Alias deletion ####
##### knoxcli.sh delete-alias n [--cluster c] [--help] #####
Deletes a password and alias mapping from a credential store within {GATEWAY_HOME}/data/security/keystores.  

argument | description
---------|-----------
--name | name of the alias to delete  
--cluster | name of Hadoop cluster for the cluster specific credential store otherwise assumes __gateway

#### Alias listing ####
##### knoxcli.sh list-alias [--cluster c] [--help] #####
Lists the alias names for the credential store within {GATEWAY_HOME}/data/security/keystores.  

argument | description
---------|-----------
--cluster	|	name of Hadoop cluster for the cluster specific credential store otherwise assumes __gateway

#### Self-signed cert creation ####
##### knoxcli.sh create-cert [--hostname n] [--help] #####
Creates and stores a self-signed certificate to represent the identity of the gateway instance. This is stored within the {GATEWAY_HOME}/data/security/keystores/gateway.jks keystore.  

argument | description
:--------|-----------
--hostname	|	name of the host to be used in the self-signed certificate. This allows multi-host deployments to specify the proper hostnames for hostname verification to succeed on the client side of the SSL connection. The default is “localhost”.

