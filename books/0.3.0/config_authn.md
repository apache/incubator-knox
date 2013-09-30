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

### Authentication ###

TODO

#### LDAP Configuration ####

TODO

##### Creation of the Key Store with self signed certificate and enabling it on Jetty

    keytool -keystore keystore -alias jetty -genkey -keyalg RSA -storepass secret

See more here about [Jetty SSL setup](http://wiki.eclipse.org/Jetty/Howto/Configure_SSL)

##### Shiro.ini file setup

###### Shiro.ini ######

    [urls]
    /** = ssl, authc

#### Session Configuration ####

Knox maps each cluster topology to a web application and leverages standard JavaEE session management.

To configure session idle timeout for the topology, please specify value of parameter sessionTimeout for ShiroProvider in your topology file.  If you do not specify the value for this parameter, it defaults to 30minutes.

The definition would look like the following in the topoloogy file:

    ...
    <provider>
                <role>authentication</role>
                <name>ShiroProvider</name>
                <enabled>true</enabled>
                <param>
                    <!-- 
                    session timeout in minutes,  this is really idle timeout,
                    defaults to 30mins, if the property value is not defined,, 
                    current client authentication would expire if client idles contiuosly for more than this value
                    -->
                    <name>sessionTimeout</name>
                    <value>30</value>
                </param>
    ...


At present, ShiroProvider in Knox leverages JavaEE session to maintain authentication state for a user across requests using JSESSIONID cookie.  So, a clieent that authenticated with Knox could pass the JSESSIONID cookie with repeated requests as long as the session has not timed out instead of submitting userid/password with every request.  Presenting a valid session cookie in place of userid/password would also perform better as additional credential store lookups are avoided.



