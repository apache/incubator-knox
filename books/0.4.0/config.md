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

### Configuration ###

#### Topology Descriptors ####

The topology descriptor files provide the gateway with per-cluster configuration information.
This includes configuration for both the providers within the gateway and the services within the Hadoop cluster.
These files are located in `{GATEWAY_HOME}/deployments`.
The general outline of this document looks like this.

    <topology>
        <gateway>
            <provider>
            </provider>
        </gateway>
        <service>
        </service>
    </topology>

There are typically multiple `<provider>` and `<service>` elements.

/topology
: Defines the provider and configuration and service topology for a single Hadoop cluster.

/topology/gateway
: Groups all of the provider elements

/topology/gateway/provider
: Defines the configuration of a specific provider for the cluster.

/topology/service
: Defines the location of a specific Hadoop service within the Hadoop cluster.

##### Provider Configuration #####

Provider configuration is used to customize the behavior of a particular gateway feature.
The general outline of a provider element looks like this.

    <provider>
        <role>authentication</role>
        <name>ShiroProvider</name>
        <enabled>true</enabled>
        <param>
            <name></name>
            <value></value>
        </param>
    </provider>

/topology/gateway/provider
: Groups information for a specific provider.

/topology/gateway/provider/role
: Defines the role of a particular provider.
There are a number of pre-defined roles used by out-of-the-box provider plugins for the gateay.
These roles are: authentication, identity-assertion, authentication, rewrite and hostmap

/topology/gateway/provider/name
: Defines the name of the provider for which this configuration applies.
There can be multiple provider implementations for a given role.
Specifying the name is used identify which particular provider is being configured.
Typically each topology descriptor should contain only one provider for each role but there are exceptions.

/topology/gateway/provider/enabled
: Allows a particular provider to be enabled or disabled via `true` or `false` respectively.
When a provider is disabled any filters associated with that provider are excluded from the processing chain.

/topology/gateway/provider/param
: These elements are used to supply provider configuration.
There can be zero or more of these per provider.

/topology/gateway/provider/param/name
: The name of a parameter to pass to the provider.

/topology/gateway/provider/param/value
: The value of a parameter to pass to the provider.

##### Service Configuration #####

Service configuration is used to specify the location of services within the Hadoop cluster.
The general outline of a service element looks like this.

    <service>
        <role>WEBHDFS</role>
        <url>http://localhost:50070/webhdfs</url>
    </service>

/topology/service
: Provider information about a particular service within the Hadoop cluster.
Not all services are necessarily exposed as gateway endpoints.

/topology/service/role
: Identifies the role of this service.
Currently supported roles are: WEBHDFS, WEBHCAT, WEBHBASE, OOZIE, HIVE, NAMENODE, JOBTRACKER
Additional service roles can be supported via plugins.

topology/service/url
: The URL identifying the location of a particular service within the Hadoop cluster.

#### Hostmap Provider ####

The purpose of the Hostmap provider is to handle situations where host are know by one name within the cluster and another name externally.
This frequently occurs when virtual machines are used and in particular using cloud hosting services.
Currently the Hostmap provider is configured as part of the topology file.
The basic structure is shown below.

    <topology>
        <gateway>
            ...
            <provider>
                <role>hostmap</role>
                <name>static</name>
                <enabled>true</enabled>
                <param><name>external-host-name</name><value>internal-host-name</value></param>
            </provider>
            ...
        </gateway>
        ...
    </topology>

This mapping is required because the Hadoop servies running within the cluster are unaware that they are being accessed from outside the cluster.
Therefore URLs returned as part of REST API responses will typically contain internal host names.
Since clients outside the cluster will be unable to resolve those host name they must be mapped to external host names.

##### Hostmap Provider Example - EC2 #####

Consider an EC2 example where two VMs have been allocated.
Each VM has an external host name by which it can be accessed via the internet.
However the EC2 VM is unaware of this external host name and instead is configured with the internal host name.

    External HOSTNAMES:
    ec2-23-22-31-165.compute-1.amazonaws.com
    ec2-23-23-25-10.compute-1.amazonaws.com

    Internal HOSTNAMES:
    ip-10-118-99-172.ec2.internal
    ip-10-39-107-209.ec2.internal

The Hostmap configuration required to allow access external to the Hadoop cluster via the Apache Knox Gateway would be this.

    <topology>
        <gateway>
            ...
            <provider>
                <role>hostmap</role>
                <name>static</name>
                <enabled>true</enabled>
                <param>
                    <name>ec2-23-22-31-165.compute-1.amazonaws.com</name>
                    <value>ip-10-118-99-172.ec2.internal</value>
                </param>
                <param>
                    <name>ec2-23-23-25-10.compute-1.amazonaws.com</name>
                    <value>ip-10-39-107-209.ec2.internal</value>
                </param>
            </provider>
            ...
        </gateway>
        ...
    </topology>

##### Hostmap Provider Example - Sandbox #####

Hortonwork's Sandbox 2.x poses a different challenge for host name mapping.
This version of the Sandbox uses port mapping to make the Sandbox VM appear as though it is accessible via localhost.
However the Sandbox VM is internally configured to consider sandbox.hortonworks.com as the host name.
So from the perspective of a client accessing Sandbox the external host name is localhost.
The Hostmap configuration required to allow access to Sandbox from the host operating system is this.

    <topology>
        <gateway>
            ...
            <provider>
                <role>hostmap</role>
                <name>static</name>
                <enabled>true</enabled>
                <param><name>localhost</name><value>sandbox,sandbox.hortonworks.com</value></param>
            </provider>
            ...
        </gateway>
        ...
    </topology>

##### Hostmap Provider Configuration #####

Details about each provider configuration element is enumerated below.

topology/gateway/provider/role
: The role for a Hostmap provider must always be `hostmap`.

topology/gateway/provider/name
: The Hostmap provider supplied out-of-the-box is selected via the name `static`.

topology/gateway/provider/enabled
: Host mapping can be enabled or disabled by providing `true` or `false`.

topology/gateway/provider/param
: Host mapping is configured by providing parameters for each external to internal mapping.

topology/gateway/provider/param/name
: The parameter names represent an external host names associated with the internal host names provided by the value element.
This can be a comma separated list of host names that all represent the same physical host.
When mapping from internal to external host name the first external host name in the list is used.

topology/gateway/provider/param/value
: The parameter values represent the internal host names associated with the external host names provider by the name element.
This can be a comma separated list of host names that all represent the same physical host.
When mapping from external to internal host names the first internal host name in the list is used.


#### Logging ####

If necessary you can enable additional logging by editing the `log4j.properties` file in the `conf` directory.
Changing the rootLogger value from `ERROR` to `DEBUG` will generate a large amount of debug logging.
A number of useful, more fine loggers are also provided in the file.


#### Java VM Options ####

TODO - Java VM options doc.


#### Persisting the Master Secret ####

The master secret is required to start the server.
This secret is used to access secured artifacts by the gateway instance.
Keystore, trust stores and credential stores are all protected with the master secret.

You may persist the master secret by supplying the *\-persist-master* switch at startup.
This will result in a warning indicating that persisting the secret is less secure than providing it at startup.
We do make some provisions in order to protect the persisted password.

It is encrypted with AES 128 bit encryption and where possible the file permissions are set to only be accessible by the user that the gateway is running as.

After persisting the secret, ensure that the file at config/security/master has the appropriate permissions set for your environment.
This is probably the most important layer of defense for master secret.
Do not assume that the encryption if sufficient protection.

A specific user should be created to run the gateway this will protect a persisted master file.


#### Management of Security Artifacts ####

There are a number of artifacts that are used by the gateway in ensuring the security of wire level communications, access to protected resources and the encryption of sensitive data.
These artifacts can be managed from outside of the gateway instances or generated and populated by the gateway instance itself.

The following is a description of how this is coordinated with both standalone (development, demo, etc) gateway instances and instances as part of a cluster of gateways in mind.

Upon start of the gateway server we:

1. Look for an identity store at `conf/security/keystores/gateway.jks`.
   The identity store contains the certificate and private key used to represent the identity of the server for SSL connections and signature creation.
    * If there is no identity store we create one and generate a self-signed certificate for use in standalone/demo mode.
      The certificate is stored with an alias of gateway-identity.
    * If there is an identity store found than we ensure that it can be loaded using the provided master secret and that there is an alias with called gateway-identity.
2. Look for a credential store at `conf/security/keystores/__gateway-credentials.jceks`.
   This credential store is used to store secrets/passwords that are used by the gateway.
   For instance, this is where the pass-phrase for accessing the gateway-identity certificate is kept.
    * If there is no credential store found then we create one and populate it with a generated pass-phrase for the alias `gateway-identity-passphrase`.
      This is coordinated with the population of the self-signed cert into the identity-store.
    * If a credential store is found then we ensure that it can be loaded using the provided master secret and that the expected aliases have been populated with secrets.

Upon deployment of a Hadoop cluster topology within the gateway we:

1. Look for a credential store for the topology. For instance, we have a sample topology that gets deployed out of the box.  We look for `conf/security/keystores/sandbox-credentials.jceks`. This topology specific credential store is used for storing secrets/passwords that are used for encrypting sensitive data with topology specific keys.
    * If no credential store is found for the topology being deployed then one is created for it.
      Population of the aliases is delegated to the configured providers within the system that will require the use of a  secret for a particular task.
      They may programmatic set the value of the secret or choose to have the value for the specified alias generated through the AliasService.
    * If a credential store is found then we ensure that it can be loaded with the provided master secret and the configured providers have the opportunity to ensure that the aliases are populated and if not to populate them.

By leveraging the algorithm described above we can provide a window of opportunity for management of these artifacts in a number of ways.

1. Using a single gateway instance as a master instance the artifacts can be generated or placed into the expected location and then replicated across all of the slave instances before startup.
2. Using an NFS mount as a central location for the artifacts would provide a single source of truth without the need to replicate them over the network. Of course, NFS mounts have their own challenges.

#### Keystores ####
In order to provide your own certificate for use by the gateway, you will need to either import an existing key pair into a Java keystore or generate a self-signed cert using the Java keytool.

##### Importing a key pair into a Java keystore #####
# ----NEEDS TESTING
One way to accomplish this is to start with a PKCS12 store for your key pair and then convert it to a Java keystore or JKS.

    openssl pkcs12 -export -in cert.pem -inkey key.pem > server.p12

The above example uses openssl to create a PKCS12 encoded store for your provided certificate private key.

    keytool -importkeystore -srckeystore {server.p12} -destkeystore gateway.jks -srcstoretype pkcs12

This example converts the PKCS12 store into a Java keystore (JKS). It should prompt you for the keystore and key passwords for the destination keystore. You must use the master-secret for both.

While using this approach a couple of important things to be aware of:

1. the alias MUST be "gateway-identity"
2. the name of the expected identity keystore for the gateway MUST be gateway.jks
3. the passwords for the keystore and the imported key MUST both be the master secret for the gateway install

NOTE: The password for the keystore as well as that of the imported key must be the master secret for the gateway instance.

# ----END NEEDS TESTING

##### Generating a self-signed cert for use in testing or development environments #####

    keytool -genkey -keyalg RSA -alias gateway-identity -keystore gateway.jks \
        -storepass {master-secret} -validity 360 -keysize 2048

Keytool will prompt you for a number of elements used that will comprise this distiniguished name (DN) within your certificate. 

*NOTE:* When it prompts you for your First and Last name be sure to type in the hostname of the machine that your gateway instance will be running on. This is used by clients during hostname verification to ensure that the presented certificate matches the hostname that was used in the URL for the connection - so they need to match.

*NOTE:* When it prompts for the key password just press enter to ensure that it is the same as the keystore password. Which as was described earlier must match the master secret for the gateway instance.

##### Credential Store #####
Whenever you provide your own keystore with either a self-signed cert or a real certificate signed by a trusted authority, you will need to create an empty credential store. This is necessary for the current release in order for the system to utilize the same password for the keystore and the key.

The credential stores in Knox use the JCEKS keystore type as it allows for the storage of general secrets in addition to certificates.

    keytool -genkey -alias {anything} -keystore __gateway-credentials.jceks \
        -storepass {master-secret} -validity 360 -keysize 1024 -storetype JCEKS

Follow the prompts again for the DN for the cert of the credential store. This certificate isn't really used for anything at the moment but is required to create the credential store.

##### Provisioning of Keystores #####
Once you have created these keystores you must move them into place for the gateway to discover them and use them to represent its identity for SSL connections. This is done by copying the keystores to the `{GATEWAY_HOME}/conf/security/keystores` directory for your gateway install.

#### Summary of Secrets to be Managed ####

1. Master secret - the same for all gateway instances in a cluster of gateways
2. All security related artifacts are protected with the master secret
3. Secrets used by the gateway itself are stored within the gateway credential store and are the same across all gateway instances in the cluster of gateways
4. Secrets used by providers within cluster topologies are stored in topology specific credential stores and are the same for the same topology across the cluster of gateway instances.
   However, they are specific to the topology - so secrets for one hadoop cluster are different from those of another.
   This allows for fail-over from one gateway instance to another even when encryption is being used while not allowing the compromise of one encryption key to expose the data for all clusters.

NOTE: the SSL certificate will need special consideration depending on the type of certificate. Wildcard certs may be able to be shared across all gateway instances in a cluster.
When certs are dedicated to specific machines the gateway identity store will not be able to be blindly replicated as host name verification problems will ensue.
Obviously, trust-stores will need to be taken into account as well.

