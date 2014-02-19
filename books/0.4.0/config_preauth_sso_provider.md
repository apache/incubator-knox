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

### Preauthenticated SSO Provider ###

A number of SSO solutions provide mechanisms for federating an authenticated identity across applications. These mechanisms are at times simple HTTP Header type tokens that can be used to propagate the identity across process boundaries.

Knox Gateway needs a pluggable mechanism for consuming these tokens and federating the asserted identity through an interaction with the Hadoop cluster. 

	CAUTION: The use of this provider requires that proper network security and identity provider configuration and deployment does not allow requests directly to the Knox gateway. Otherwise, this provider will leave the gateway exposed to identity spoofing.

#### Configuration ####
##### Overview #####
The HeaderPreAuth provider is configured within the topology file and has a minimal configuration that assumes SM_USER for CA SiteMinder. The following example is the bare minimum configuration for SiteMinder (with no IP address validation).

	<provider>
      <role>federation</role>
      <name>HeaderPreAuth</name>
      <enabled>true</enabled>
    </provider>

The following table describes the configuration options for the web app security provider:

##### Descriptions #####

Name | Description | Default
---------|-----------
preauth.validation.method|Optional parameter that indicates the type of trust validation to perform on incoming requests. Possible values are: null, preauth.ip.validation (others will be added in future releases). Failure results in a 403 forbidden HTTP status response.|null - which means no validation will be performed and that we are assuming that the network security and external authentication system is sufficient.  
preauth.ip.addresses|Optional parameter that indicates the list of trusted ip addresses. When preauth.ip.validation is indicated as the validation method this parameter must be provided to indicate the trusted ip address set. Wildcarded IPs may be used to indicate subnet level trust. ie. 127.0.*|null - which means that no validation will be performed.
preauth.custom.header|Required parameter for indicating a custom header to use for extracting the preauthenticated principal. The value extracted from this header is utilized as the PrimaryPrincipal within the established Subject. An incoming request that is missing the configured header will be refused with a 401 unauthorized HTTP status.|SM_USER for SiteMinder usecase
preauth.custom.group.header|Optional parameter for indicating a HTTP header name that contains a comma separated list of groups. These are added to the authenticated Subject as group principals. A missing group header will result in no groups being extracted from the incoming request and a log entry but processing will continue.|null - which means that there will be no group principals extracted from the request and added to the established Subject.

##### Configuration for SiteMinder
The following is an example of a configuration of the preauthenticated sso provider that leverages the default SM_USER header name - assuming use with CA SiteMinder. It further configures the validation based on the IP address from the incoming request.

	<provider>
      <role>federation</role>
      <name>HeaderPreAuth</name>
      <enabled>true</enabled>
      <param><name>preauth.validation.method</name><value>preauth.ip.validation</value></param>
      <param><name>preauth.ip.addresses</name><value>127.0.0.2,127.0.0.1</value></param>
    </provider>

##### REST Invocation for SiteMinder
The following curl command can be used to request a directory listing from HDFS while passing in the expected header SM_USER.

	curl -k -i --header "SM_USER: guest" -v https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp?op=LISTSTATUS

Omitting the --header "SM_USER: guest" above will result in a rejected request.

##### Configuration for IBM Tivoli AM
As an example for configuring the preauthenticated sso provider for another SSO provider, the following illustrates the values used for IBM's Tivoli Access Manager:

	<provider>
      <role>federation</role>
      <name>HeaderPreAuth</name>
      <enabled>true</enabled>
      <param><name>preauth.custom.header</name><value>iv_user</value></param>
      <param><name>preauth.custom.group.header</name><value>iv_group</value></param>
      <param><name>preauth.validation.method</name><value>preauth.ip.validation</value></param>
      <param><name>preauth.ip.addresses</name><value>127.0.0.2,127.0.0.1</value></param>
    </provider>

##### REST Invocation for Tivoli AM
The following curl command can be used to request a directory listing from HDFS while passing in the expected headers of iv_user and iv_group. Note that the iv_group value in this command matches the expected ACL for webhdfs in the above topology file. Changing this from “admin” to “admin2” should result in a 401 unauthorized response.

	curl -k -i --header "iv_user: guest" --header "iv_group: admin" -v https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp?op=LISTSTATUS

Omitting the --header "iv_user: guest" above will result in a rejected request.
