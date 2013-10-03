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

### Identity Assertion ###
The identity assertion provider within Knox plays the critical role of communicating the identity principal to be used within the Hadoop cluster to represent the identity that has been authenticated at the gateway.

The general responsibilities of the identity assertion provider is to interrogate the current Java Subject that has been established by the authentication or federation provider and:

1. determine whether it matches any principal mapping rules and apply them appropriately
2. determine whether it matches any group principal mapping rules and apply them
3. if it is determined that the principal will be impersonating another through a principal mapping rule then a Subject.doAS is required in order for providers farther downstream can determine the appropriate effective principal name and groups for the user

The following configuration is required for asserting the users identity to the Hadoop cluster using Pseudo or Simple "authentication".

    <provider>
        <role>identity-assertion</role>
        <name>Pseudo</name>
        <enabled>true</enabled>
    </provider>

This particular configuration indicates that the Pseudo identity assertion provider is enabled and that there are no principal mapping rules to apply to identities flowing from the authentication in the gateway to the backend Hadoop cluster services. The primary principal of the current subject will therefore be asserted via a query paramter or as a form parameter - ie. ?user.name={primaryPrincipal}

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

This configuration identifies the same identity assertion provider but does provide principal and group mapping rules. In this case, when a user is authenticated as "bob" his identity is actually asserted to the Hadoop cluster as "hdfs". In addition, since there are group principal mappings defined, he will also be considered as a member of the groups "users" and "admin". In this particular example the wildcard "*" is used to indicate that all authenticated users need to be considered members of the "users" group and that only the user "hdfs" is mapped to be a member of the "admin" group.

	NOTE: These group memberships are currently only meaningful for Service Level Authorization using the AclsAuthorization provider. The groups are not currently asserted to the Hadoop cluster at this time. See the Authorization section within this guide to see how this is used.

The principal mapping aspect of the identity assertion provider is important to understand in order to fully utilize the authorization features of this provider.

This feature allows us to map the authenticated principal to a runas or impersonated principal to be asserted to the Hadoop services in the backend.

When a principal mapping is defined that results in an impersonated principal being created the impersonated principal is then the effective principal.

If there is no mapping to another principal then the authenticated or primary principal is then the effective principal.

#### Principal Mapping ####

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
        <value>bob,alice=hdfs;mary=alice2</value>
    </param>

#### Group Principal Mapping ####

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


