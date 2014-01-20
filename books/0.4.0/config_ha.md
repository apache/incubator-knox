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

### High Availability ###

#### Configure Knox instances ####

All Knox instances must be synced to use the same topologies credentials keystores.
These files are located under {GATEWAY_HOME}/conf/security/keystores/{TOPOLOGY_NAME}-credentials.jceks.
They are generated after the first topology deployment.
Currently these files can be synced just manually. There is no automation tool.
Here are the steps to sync topologies credentials keystores:

1. Choose Knox instance that will be the source for topologies credentials keystores. Let's call it keystores master
1. Replace topologies credentials keystores in the other Knox instance with topologies credentials keystores from keystores master
1. Restart Knox instances

#### High Availability with Apache HTTP Server + mod_proxy + mod_proxy_balancer ####

##### 1 - Requirements #####

###### openssl-devel ######

openssl-devel is required for Apache Module mod_ssl.

    sudo yum install openssl-devel

###### Apache HTTP Server ######

Apache HTTP Server 2.4.6 or later is required. See this document for installing and setting up Apache HTTP Server: http://httpd.apache.org/docs/2.4/install.html

Hint: pass --enable-ssl to ./configure command to enable Apache Module mod_ssl generation.

###### Apache Module mod_proxy ######

See this document for setting up Apache Module mod_proxy: http://httpd.apache.org/docs/2.4/mod/mod_proxy.html

###### Apache Module mod_proxy_balancer ######

See this document for setting up Apache Module mod_proxy_balancer: http://httpd.apache.org/docs/2.4/mod/mod_proxy_balancer.html

###### Apache Module mod_ssl ######

See this document for setting up Apache Module mod_ssl: http://httpd.apache.org/docs/2.4/mod/mod_ssl.html

##### 2 - Configuration example #####

###### Generate certificate for Apache HTTP Server ######

See this document for an example: http://www.akadia.com/services/ssh_test_certificate.html

By convention, Apache HTTP Server and Knox certificates are put into /etc/apache2/ssl/ folder.

###### Update Apache HTTP Server configuration file ######

This file is located under {APACHE_HOME}/conf/httpd.conf.

Following directives have to be added or uncommented in the configuration file:

* LoadModule proxy_module modules/mod_proxy.so
* LoadModule proxy_http_module modules/mod_proxy_http.so
* LoadModule proxy_balancer_module modules/mod_proxy_balancer.so
* LoadModule ssl_module modules/mod_ssl.so
* LoadModule lbmethod_byrequests_module modules/mod_lbmethod_byrequests.so
* LoadModule lbmethod_bytraffic_module modules/mod_lbmethod_bytraffic.so
* LoadModule lbmethod_bybusyness_module modules/mod_lbmethod_bybusyness.so
* LoadModule lbmethod_heartbeat_module modules/mod_lbmethod_heartbeat.so
* LoadModule slotmem_shm_module modules/mod_slotmem_shm.so

Also following lines have to be added to file. Replace placeholders (${...}) with real data:

    Listen 443
    <VirtualHost *:443>
       SSLEngine On
       SSLProxyEngine On
       SSLCertificateFile ${PATH_TO_CERTICICATE_FILE}
       SSLCertificateKeyFile ${PATH_TO_CERTICICATE_KEY_FILE}
       SSLProxyCACertificateFile ${PATH_TO_PROXY_CA_CERTICICATE_FILE}

       ProxyRequests Off
       ProxyPreserveHost Off

       Header add Set-Cookie "ROUTEID=.%{BALANCER_WORKER_ROUTE}e; path=/" env=BALANCER_ROUTE_CHANGED
       <Proxy balancer://mycluster>
         BalancerMember ${HOST_#1} route=1
         BalancerMember ${HOST_#2} route=2
         ...
         BalancerMember ${HOST_#N} route=N

         ProxySet failontimeout=On lbmethod=${LB_METHOD} stickysession=ROUTEID 
       </Proxy>

       ProxyPass / balancer://mycluster/
       ProxyPassReverse / balancer://mycluster/
    </VirtualHost>

Note:

* SSLProxyEngine enables SSL between Apache HTTP Server and Knox instances;
* SSLCertificateFile and SSLCertificateKeyFile have to point to certificate data of Apache HTTP Server. User will use this certificate for communications with Apache HTTP Server;
* SSLProxyCACertificateFile has to point to Knox certificates.

###### Start/stop Apache HTTP Server ######

    APACHE_HOME/bin/apachectl -k start
    APACHE_HOME/bin/apachectl -k stop

###### Verify ######

Use Knox samples.
