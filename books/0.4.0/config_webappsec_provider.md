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

### Web App Security Provider ###
Knox is a Web API (REST) Gateway for Hadoop. The fact that REST interactions are HTTP based means that they are vulnerable to a number of web application security vulnerabilities. This project introduces a web application security provider for plugging in various protection filters.

The initial vulnerability protection filter will be for Cross Site Request Forgery (CSRF). Others will be added in future releases.
 
Cross site request forgery (CSRF) attacks attempt to force an authenticated user to 
execute functionality without their knowledge. By presenting them with a link or image that when clicked invokes a request to another site with which the user may have already established an active session.

CSRF is entirely a browser based attack. Some background knowledge of how browsers work enables us to provide a filter that will prevent CSRF attacks. HTTP requests from a web browser performed via form, image, iframe, etc are unable to set custom HTTP headers. The only way to create a HTTP request from a browser with a custom HTTP header is to use a technology such as Javascript XMLHttpRequest or Flash. These technologies can set custom HTTP headers, but have security policies built in to prevent web sites from sending requests to each other 
unless specifically allowed by policy. 

This means that a website www.bad.com cannot send a request to  http://bank.example.com with the custom header X-XSRF-Header unless they use a technology such as a XMLHttpRequest. That technology  would prevent such a request from being made unless the bank.example.com domain specifically allowed it. This then results in a REST endpoint that can only be called via XMLHttpRequest (or similar technology).

NOTE: by enabling this protection within the gateway, this custom header will be required for *all* clients that interact with it - not just browsers.


#### Configuration ####
##### Overview #####
As with all providers in the Knox gateway, the web app security provider is configured through provider params. Unlike many other providers, the web app security provider may actually host multiple vulnerability filters. Currently, we only have an implementation for CSRF but others will follow and you may be interested in creating your own.

Because of this one-to-many provider/filter relationship, there is an extra configuration element for this provider per filter. As you can see in the sample below, the actual filter configuration is defined entirely within the params of the WebAppSec provider.

	<provider
	  <role>webappsec</role>
	  <name>WebAppSec</name>
	  <enabled>true</enabled>
	  <param><name>csrf.enabled</name><value>true</value></param>
	  <param><name>csrf.customHeader</name><value>X-XSRF-Header</value></param>
	  <param><name>csrf.methodsToIgnore</name><value>GET,OPTIONS,HEAD</value></param>
	</provider>

#### Descriptions ####
The following table describes the configuration options for the web app security provider:

Name | Description | Default
---------|-----------
csrf.enabled|This param enables the CSRF protection capabilities|false  
csrf.customHeader|This is an optional param that indicates the name of the header to be used in order to determine that the request is from a trusted source. It defaults to the header name described by the NSA in its guidelines for dealing with CSRF in REST.|X-XSRF-Header
csrf.methodsToIgnore|This is also an optional param that enumerates the HTTP methods to allow through without the custom HTTP header. This is useful for allowing things like GET requests from the URL bar of a browser but it assumes that the GET request adheres to REST principals in terms of being idempotent. If this cannot be assumed then it would be wise to not include GET in the list of methods to ignore.|GET,OPTIONS,HEAD

#### REST Invocation
The following curl command can be used to request a directory listing from HDFS while passing in the expected header X-XSRF-Header.

	curl -k -i --header "X-XSRF-Header: valid" -v -u guest:guest-password https://localhost:8443/gateway/sandbox/webhdfs/v1/tmp?op=LISTSTATUS

Omitting the --header "X-XSRF-Header: valid" above should result in an HTTP 400 bad_request.

Disabling the provider will then allow a request that is missing the header through. 

