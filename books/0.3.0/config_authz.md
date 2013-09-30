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

### Authorization ###

#### Service Level Authorization ####

The Knox Gateway has an out-of-the-box authorization provider that allows administrators to restrict access to the individual services within a Hadoop cluster.

This provider utilizes a simple and familiar pattern of using ACLs to protect Hadoop resources by specifying users, groups and ip addresses that are permitted access.

Note: In the examples below \{serviceName\} represents a real service name (e.g. WEBHDFS) and would be replaced with these values in an actual configuration.

##### Usecases #####

###### USECASE-1: Restrict access to specific Hadoop services to specific Users

    <param>
        <name>{serviceName}.acl</name>
        <value>bob;*;*</value>
    </param>

###### USECASE-2: Restrict access to specific Hadoop services to specific Groups

    <param>
        <name>{serviceName}.acls</name>
        <value>*;admins;*</value>
    </param>

###### USECASE-3: Restrict access to specific Hadoop services to specific Remote IPs

    <param>
        <name>{serviceName}.acl</name>
        <value>*;*;127.0.0.1</value>
    </param>

###### USECASE-4: Restrict access to specific Hadoop services to specific Users OR users within specific Groups

    <param>
        <name>{serviceName}.acl.mode</name>
        <value>OR</value>
    </param>
    <param>
        <name>{serviceName}.acl</name>
        <value>bob;admin;*</value>
    </param>

###### USECASE-5: Restrict access to specific Hadoop services to specific Users OR users from specific Remote IPs

    <param>
        <name>{serviceName}.acl.mode</name>
        <value>OR</value>
    </param>
    <param>
        <name>{serviceName}.acl</name>
        <value>bob;*;127.0.0.1</value>
    </param>

###### USECASE-6: Restrict access to specific Hadoop services to users within specific Groups OR from specific Remote IPs

    <param>
        <name>{serviceName}.acl.mode</name>
        <value>OR</value>
    </param>
    <param>
        <name>{serviceName}.acl</name>
        <value>*;admin;127.0.0.1</value>
    </param>

###### USECASE-7: Restrict access to specific Hadoop services to specific Users OR users within specific Groups OR from specific Remote IPs

    <param>
        <name>{serviceName}.acl.mode</name>
        <value>OR</value>
    </param>
    <param>
        <name>{serviceName}.acl</name>
        <value>bob;admin;127.0.0.1</value>
    </param>

###### USECASE-8: Restrict access to specific Hadoop services to specific Users AND users within specific Groups

    <param>
        <name>{serviceName}.acl</name>
        <value>bob;admin;*</value>
    </param>

###### USECASE-9: Restrict access to specific Hadoop services to specific Users AND users from specific Remote IPs

    <param>
        <name>{serviceName}.acl</name>
        <value>bob;*;127.0.0.1</value>
    </param>

###### USECASE-10: Restrict access to specific Hadoop services to users within specific Groups AND from specific Remote IPs

    <param>
        <name>{serviceName}.acl</name>
        <value>*;admins;127.0.0.1</value>
    </param>

###### USECASE-11: Restrict access to specific Hadoop services to specific Users AND users within specific Groups AND from specific Remote IPs

    <param>
        <name>{serviceName}.acl</name>
        <value>bob;admins;127.0.0.1</value>
    </param>

#### Configuration ####

ACLs are bound to services within the topology descriptors by introducing the authorization provider with configuration like:

    <provider>
        <role>authorization</role>
        <name>AclsAuthz</name>
        <enabled>true</enabled>
    </provider>

The above configuration enables the authorization provider but does not indicate any ACLs yet and therefore there is no restriction to accessing the Hadoop services. In order to indicate the resources to be protected and the specific users, groups or ip's to grant access, we need to provide parameters like the following:

    <param>
        <name>{serviceName}.acl</name>
        <value>username[,*|username...];group[,*|group...];ipaddr[,*|ipaddr...]</value>
    </param>
    
where `{serverName}` would need to be the name of a configured Hadoop service within the topology.

NOTE: ipaddr is unique among the parts of the ACL in that you are able to specify a wildcard within an ipaddr to indicate that the remote address must being with the String prior to the asterisk within the ipaddr acl. For instance:

    <param>
        <name>{serviceName}.acl</name>
        <value>*;*;192.168.*</value>
    </param>
    
This indicates that the request must come from an IP address that begins with '192.168.' in order to be granted access.

Note also that configuration without any ACLs defined is equivalent to:

    <param>
        <name>{serviceName}.acl</name>
        <value>*;*;*</value>
    </param>

meaning: all users, groups and IPs have access.
Each of the elements of the acl param support multiple values via comma separated list and the `*` wildcard to match any.

For instance:

    <param>
        <name>namenode.acl</name>
        <value>hdfs;admin;127.0.0.2,127.0.0.3</value>
    </param>

this configuration indicates that ALL of the following are satisfied:

1. the user "hdfs" has access AND
2. users in the group "admin" have access AND
3. any authenticated user from either 127.0.0.2 or 127.0.0.3 will have access

This allows us to craft policy that restricts the members of a large group to a subset that should have access.
The user being removed from the group will allow access to be denied even though their username may have been in the ACL.

An additional configuration element may be used to alter the processing of the ACL to be OR instead of the default AND behavior:

    <param>
        <name>{serviceName}.acl.mode</name>
        <value>OR</value>
    </param>

this processing behavior requires that the effective user satisfy one of the parts of the ACL definition in order to be granted access.
For instance:

    <param>
        <name>namenode.acl</name>
        <value>hdfs,bob;admin;127.0.0.2,127.0.0.3</value>
    </param>

You may also set the ACL processing mode at the top level for the topology. This essentially sets the default for the managed cluster.
It may then be overridden at the service level as well.

    <param>
        <name>acl.mode</name>
        <value>OR</value>
    </param>

this configuration indicates that ONE of the following must be satisfied to be granted access:

1. the user is "hdfs" or "bob" OR
2. the user is in "admin" group OR
3. the request is coming from 127.0.0.2 or 127.0.0.3

#### Other Related Configuration ####

The principal mapping aspect of the identity assertion provider is important to understand in order to fully utilize the authorization features of this provider.

This feature allows us to map the authenticated principal to a runas or impersonated principal to be asserted to the Hadoop services in the backend.
When a principal mapping is defined that results in an impersonated principal being created the impersonated principal is then the effective principal.
If there is no mapping to another principal then the authenticated or primary principal is then the effective principal.
Principal mapping has actually been available in the identity assertion provider from the beginning of Knox.
Although hasn't been adequately documented as of yet.

    <param>
        <name>principal.mapping</name>
        <value>{primaryPrincipal}[,...]={impersonatedPrincipal}[;...]</value>
    </param>

For instance:

    <param>
        <name>principal.mapping</name>
        <value>bob=hdfs</value>
    </param>

For multiple mappings:

    <param>
        <name>principal.mapping</name>
        <value>bob=hdfs;alice=alice2</value>
    </param>

More context for the identity assertion provider config:

    <provider>
        <role>identity-assertion</role>
        <name>Pseudo</name>
        <enabled>true</enabled>
        <param>
            <name>principal.mapping</name>
            <value>bob=hdfs;</value>
        </param>
        <param>
            <name>group.principal.mapping</name>
            <value>*=users;hdfs=admin</value>
        </param>
    </provider>

this configuration defines a principal mapping between an incoming identity of "bob" to an impersonated principal of "hdfs".
For an authenticated request from bob the effective principal ends up being "hdfs".

In addition, we allow the administrator to map groups to effective principals. This is done through another param within the identity assertion provider:

    <param>
        <name>group.principal.mapping</name>
        <value>{userName[,*|userName...]}={groupName[,groupName...]}[,...]</value>
    </param>

For instance:

    <param>
        <name>group.principal.mapping</name>
        <value>*=users;hdfs=admin</value>
    </param>

this configuration indicates that all (*) authenticated users are members of the "users" group and that user "hdfs" is a member of the admin group. Group principal mapping has been added along with the authorization provider described in this document.

These additional mapping capabilities are used together with the authorization ACL policy.
An example of a full topology that illustrates these together is below.

    <topology>
        <gateway>
            <provider>
                <role>authentication</role>
                <name>ShiroProvider</name>
                <enabled>true</enabled>
                <param>
                    <name>main.ldapRealm</name>
                    <value>org.apache.shiro.realm.ldap.JndiLdapRealm</value>
                </param>
                <param>
                    <name>main.ldapRealm.userDnTemplate</name>
                    <value>uid={0},ou=people,dc=hadoop,dc=apache,dc=org</value>
                </param>
                <param>
                    <name>main.ldapRealm.contextFactory.url</name>
                    <value>ldap://localhost:33389</value>
                </param>
                <param>
                    <name>main.ldapRealm.contextFactory.authenticationMechanism</name>
                    <value>simple</value>
                </param>
                <param>
                    <name>urls./**</name>
                    <value>authcBasic</value>
                </param>
            </provider>
            <provider>
                <role>identity-assertion</role>
                <name>Pseudo</name>
                <enabled>true</enabled>
                <param>
                    <name>principal.mapping</name>
                    <value>bob=hdfs;</value>
                </param>
                <param>
                    <name>group.principal.mapping</name>
                    <value>*=users;hdfs=admin</value>
                </param>
            </provider>

            <provider>
                <role>authorization</role>
                <name>AclsAuthz</name>
                <enabled>true</enabled>
                <param>
                    <name>acl.mode</name>
                    <value>OR</value>
                </param>
                <param>
                    <name>namenode.acl.mode</name>
                    <value>AND</value>
                </param>
                <param>
                    <name>namenode.acl</name>
                    <value>hdfs;admin;127.0.0.2,127.0.0.3</value>
                </param>
                <param>
                    <name>templeton.acl</name>
                    <value>hdfs;admin;127.0.0.2,127.0.0.3</value>
                </param>
            </provider>
            <provider>
                <role>hostmap</role>
                <name>static</name>
                <enabled>true</enabled>
                <param>
                    <name>localhost</name>
                    <value>sandbox,sandbox.hortonworks.com</value>
                </param>
            </provider>
        </gateway>

        <service>
            <role>NAMENODE</role>
            <url>http://localhost:50070/webhdfs/v1</url>
        </service>
        <service>
            <role>TEMPLETON</role>
            <url>http://localhost:50111/templeton/v1</url>
        </service>
        <service>
            <role>OOZIE</role>
            <url>http://localhost:11000/oozie</url>
        </service>
        <service>
            <role>HBASE</role>
            <url>http://localhost:60000/</url>
        </service>
        <service>
            <role>HIVE</role>
            <url>http://localhost:10000/</url>
        </service>
    </topology>
