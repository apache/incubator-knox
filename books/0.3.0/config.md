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

{{Configuration}}
-----------------

### Host Mapping ###

TODO

That really depends upon how you have your VM configured.
If you can hit http://c6401.ambari.apache.org:1022/ directly from your client and knox host then you probably don't need the hostmap at all.
The host map only exists for situations where a host in the hadoop cluster is known by one name externally and another internally.
For example running hostname -q on sandbox returns sandbox.hortonworks.com but externally Sandbox is setup to be accesses using localhost via portmapping.
The way the hostmap config works is that the <name/> element is what the hadoop cluster host is known as externally and the <value/> is how the hadoop cluster host identifies itself internally.
<param><name>localhost</name><value>c6401,c6401.ambari.apache.org</value></param>
You SHOULD be able to simply change <enabled>true</enabled> to false but I have a suspicion that that might not actually work.
Please try it and file a jira if that doesn't work.
If so, simply either remove the full provider config for hostmap or remove the <param/> that defines the mapping.


### Logging ###

If necessary you can enable additional logging by editing the `log4j.properties` file in the `conf` directory.
Changing the rootLogger value from `ERROR` to `DEBUG` will generate a large amount of debug logging.
A number of useful, more fine loggers are also provided in the file.


### Java VM Options ###

TODO


### Persisting the Master Secret ###

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


### Management of Security Artifacts ###

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

1. Look for a credential store for the topology. For instance, we have a sample topology that gets deployed out of the box.  We look for `conf/security/keystores/sample-credentials.jceks`. This topology specific credential store is used for storing secrets/passwords that are used for encrypting sensitive data with topology specific keys.
    * If no credential store is found for the topology being deployed then one is created for it.
      Population of the aliases is delegated to the configured providers within the system that will require the use of a  secret for a particular task.
      They may programmatic set the value of the secret or choose to have the value for the specified alias generated through the AliasService.
    * If a credential store is found then we ensure that it can be loaded with the provided master secret and the configured providers have the opportunity to ensure that the aliases are populated and if not to populate them.

By leveraging the algorithm described above we can provide a window of opportunity for management of these artifacts in a number of ways.

1. Using a single gateway instance as a master instance the artifacts can be generated or placed into the expected location and then replicated across all of the slave instances before startup.
2. Using an NFS mount as a central location for the artifacts would provide a single source of truth without the need to replicate them over the network. Of course, NFS mounts have their own challenges.

Summary of Secrets to be Managed:

1. Master secret - the same for all gateway instances in a cluster of gateways
2. All security related artifacts are protected with the master secret
3. Secrets used by the gateway itself are stored within the gateway credential store and are the same across all gateway instances in the cluster of gateways
4. Secrets used by providers within cluster topologies are stored in topology specific credential stores and are the same for the same topology across the cluster of gateway instances.
   However, they are specific to the topology - so secrets for one hadoop cluster are different from those of another.
   This allows for fail-over from one gateway instance to another even when encryption is being used while not allowing the compromise of one encryption key to expose the data for all clusters.

NOTE: the SSL certificate will need special consideration depending on the type of certificate. Wildcard certs may be able to be shared across all gateway instances in a cluster.
When certs are dedicated to specific machines the gateway identity store will not be able to be blindly replicated as hostname verification problems will ensue.
Obviously, trust-stores will need to be taken into account as well.

