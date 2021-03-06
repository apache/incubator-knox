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

### Secure Clusters ###

See these documents for setting up a secure Hadoop cluster
http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/ClusterSetup.html#Configuration_in_Secure_Mode
http://docs.hortonworks.com/HDPDocuments/HDP1/HDP-1.3.1/bk_installing_manually_book/content/rpm-chap14.html

Once you have a Hadoop cluster that is using Kerberos for authentication, you have to do the following to configure Knox to work with that cluster.

#### Create Unix account for Knox on Hadoop master nodes ####

    useradd -g hadoop knox

#### Create Kerberos principal, keytab for Knox ####

One way of doing this, assuming your KDC realm is EXAMPLE.COM, is to ssh into your host running KDC and execute `kadmin.local`
That will result in an interactive session in which you can execute commands.

ssh into your host running KDC

    kadmin.local
    add_principal -randkey knox/knox@EXAMPLE.COM
    ktadd -norandkey -k /etc/security/keytabs/knox.service.keytab
    ktadd -k /etc/security/keytabs/knox.service.keytab -norandkey knox/knox@EXAMPLE.COM
    exit

#### Grant Proxy privileges for Knox user in `core-site.xml` on Hadoop master nodes ####

Update `core-site.xml` and add the following lines towards the end of the file.

Replace FQDN_OF_KNOX_HOST with the fully qualified domain name of the host running the gateway.
You can usually find this by running `hostname -f` on that host.

You could use * for local developer testing if Knox host does not have static IP.

    <property>
        <name>hadoop.proxyuser.knox.groups</name>
        <value>users</value>
    </property>
    <property>
        <name>hadoop.proxyuser.knox.hosts</name>
        <value>FQDN_OF_KNOX_HOST</value>
    </property>

#### Grant proxy privilege for Knox in `webhcat-stie.xml` on Hadoop master nodes ####

Update `webhcat-site.xml` and add the following lines towards the end of the file.

Replace FQDN_OF_KNOX_HOST with right value in your cluster.
You could use * for local developer testing if Knox host does not have static IP.

    <property>
        <name>hadoop.proxyuser.knox.groups</name>
        <value>users</value>
    </property>
    <property>
        <name>hadoop.proxyuser.knox.hosts</name>
        <value>FQDN_OF_KNOX_HOST</value>
    </property>

#### Grant proxy privilege for Knox in `oozie-stie.xml` on Oozie host ####

Update `oozie-site.xml` and add the following lines towards the end of the file.

Replace FQDN_OF_KNOX_HOST with right value in your cluster.
You could use * for local developer testing if Knox host does not have static IP.

    <property>
       <name>oozie.service.ProxyUserService.proxyuser.knox.groups</name>
       <value>users</value>
    </property>
    <property>
       <name>oozie.service.ProxyUserService.proxyuser.knox.hosts</name>
       <value>FQDN_OF_KNOX_HOST</value>
    </property>

#### Copy knox keytab to Knox host ####

Add unix account for the knox user on Knox host

    useradd -g hadoop knox

Copy knox.service.keytab created on KDC host on to your Knox host /etc/knox/conf/knox.service.keytab

    chown knox knox.service.keytab
    chmod 400 knox.service.keytab

#### Update krb5.conf at /etc/knox/conf/krb5.conf on Knox host ####

You could copy the `templates/krb5.conf` file provided in the Knox binary download and customize it to suit your cluster.

#### Update `krb5JAASLogin.conf` at `/etc/knox/conf/krb5JAASLogin.conf` on Knox host ####

You could copy the `templates/krb5JAASLogin.conf` file provided in the Knox binary download and customize it to suit your cluster.

#### Update `gateway-site.xml` on Knox host on Knox host ####

Update `conf/gateway-site.xml` in your Knox installation and set the value of `gateway.hadoop.kerberos.secured` to true.

#### Restart Knox ####

After you do the above configurations and restart Knox, Knox would use SPNego to authenticate with Hadoop services and Oozie.
There is no change in the way you make calls to Knox whether you use Curl or Knox DSL.
