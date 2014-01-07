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

### Hive ###

TODO

#### Hive URL Mapping ####

TODO

#### Hive Examples ####

This guide provides detailed examples for how to to some basic interactions with Hive via the Apache Knox Gateway.

##### Hive Setup #####

1. Make sure you are running the correct version of Hive to ensure JDBC/Thrift/HTTP support.
2. Make sure Hive is running on the correct port.
3. In hive-server.xml add the property "hive.server2.servermode=http"
4. Client side (JDBC):
    1. Hive JDBC in HTTP mode depends on following libraries to run successfully(must be in the classpath):
       Hive Thrift artifacts classes, commons-codec.jar, commons-configuration.jar, commons-lang.jar, commons-logging.jar, hadoop-core.jar, hive-cli.jar, hive-common.jar, hive-jdbc.jar, hive-service.jar, hive-shims.jar, httpclient.jar, httpcore.jar, slf4j-api.jar;
    2. Import gateway certificate into the default JRE truststore.
       It is located in the `/lib/security/cacerts`
          `keytool -import -alias hadoop.gateway -file hadoop.gateway.cer -keystore <java-home>/lib/security/cacerts`
       Alternatively you can run your sample with additional parameters:
          `-Djavax.net.ssl.trustStoreType=JKS -Djavax.net.ssl.trustStore=<path-to-trust-store> -Djavax.net.ssl.trustStorePassword=<trust-store-password>`
    3. Connection URL has to be following:
       `jdbc:hive2://<gateway-host>:<gateway-port>/?hive.server2.servermode=https;hive.server2.http.path=<gateway-path>/<cluster-name>/hive`
    4. Look at https://cwiki.apache.org/confluence/display/Hive/GettingStarted#GettingStarted-DDLOperations for examples.
       Hint: For testing it would be better to execute "set hive.security.authorization.enabled=false" as the first statement.
       Hint: Good examples of Hive DDL/DML can be found here http://gettingstarted.hadooponazure.com/hw/hive.html

##### Customization #####

This example may need to be tailored to the execution environment.
In particular host name, host port, user name, user password and context path may need to be changed to match your environment.
In particular there is one example file in the distribution that may need to be customized.
Take a moment to review this file.
All of the values that may need to be customized can be found together at the top of the file.

* samples/HiveJDBCSample.java

##### Client JDBC Example #####

Sample example for creating new table, loading data into it from local file system and querying data from that table.

###### Java ######

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.sql.Statement;

    import java.util.logging.Level;
    import java.util.logging.Logger;

    public class HiveJDBCSample {

      public static void main( String[] args ) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
          String user = "guest";
          String password = user + "-password";
          String gatewayHost = "localhost";
          int gatewayPort = 8443;
          String contextPath = "gateway/sandbox/hive";
          String connectionString = String.format( "jdbc:hive2://%s:%d/?hive.server2.servermode=https;hive.server2.http.path=%s", gatewayHost, gatewayPort, contextPath );

          // load Hive JDBC Driver
          Class.forName( "org.apache.hive.jdbc.HiveDriver" );

          // configure JDBC connection
          connection = DriverManager.getConnection( connectionString, user, password );

          statement = connection.createStatement();

          // disable Hive authorization - it could be ommited if Hive authorization
          // was configured properly
          statement.execute( "set hive.security.authorization.enabled=false" );

          // create sample table
          statement.execute( "CREATE TABLE logs(column1 string, column2 string, column3 string, column4 string, column5 string, column6 string, column7 string) ROW FORMAT DELIMITED FIELDS TERMINATED BY ' '" );

          // load data into Hive from file /tmp/log.txt which is placed on the local file system
          statement.execute( "LOAD DATA LOCAL INPATH '/tmp/log.txt' OVERWRITE INTO TABLE logs" );

          resultSet = statement.executeQuery( "SELECT * FROM logs" );

          while ( resultSet.next() ) {
            System.out.println( resultSet.getString( 1 ) + " --- " + resultSet.getString( 2 ) + " --- " + resultSet.getString( 3 ) + " --- " + resultSet.getString( 4 ) );
          }
        } catch ( ClassNotFoundException ex ) {
          Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( SQLException ex ) {
          Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
        } finally {
          if ( resultSet != null ) {
            try {
              resultSet.close();
            } catch ( SQLException ex ) {
              Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
            }
          }
          if ( statement != null ) {
            try {
              statement.close();
            } catch ( SQLException ex ) {
              Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
            }
          }
          if ( connection != null ) {
            try {
              connection.close();
            } catch ( SQLException ex ) {
              Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
            }
          }
        }
      }
    }

h3. Groovy

Make sure that GATEWAY_HOME/ext directory contains following jars/classes for successful execution:
Hive Thrift artifacts classes, commons-codec.jar, commons-configuration.jar, commons-lang.jar, commons-logging.jar, hadoop-core.jar, hive-cli.jar, hive-common.jar, hive-jdbc.jar, hive-service.jar, hive-shims.jar, httpclient.jar, httpcore.jar, slf4j-api.jar

There are several ways to execute this sample depending upon your preference.

You can use the Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar samples/hive/groovy/jdbc/sandbox/HiveJDBCSample.groovy

You can manually type in the KnoxShell DSL script into the interactive Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar

Each line from the file below will need to be typed or copied into the interactive shell.

    import java.sql.DriverManager

    user = "guest";
    password = user + "-password";
    gatewayHost = "localhost";
    gatewayPort = 8443;
    contextPath = "gateway/sandbox/hive";
    connectionString = String.format( "jdbc:hive2://%s:%d/?hive.server2.servermode=https;hive.server2.http.path=%s", gatewayHost, gatewayPort, contextPath );

    // Load Hive JDBC Driver
    Class.forName( "org.apache.hive.jdbc.HiveDriver" );

    // Configure JDBC connection
    connection = DriverManager.getConnection( connectionString, user, password );

    statement = connection.createStatement();

    // Disable Hive authorization - This can be ommited if Hive authorization is configured properly
    statement.execute( "set hive.security.authorization.enabled=false" );

    // Create sample table
    statement.execute( "CREATE TABLE logs(column1 string, column2 string, column3 string, column4 string, column5 string, column6 string, column7 string) ROW FORMAT DELIMITED FIELDS TERMINATED BY ' '" );

    // Load data into Hive from file /tmp/log.txt which is placed on the local file system
    statement.execute( "LOAD DATA LOCAL INPATH '/tmp/sample.log' OVERWRITE INTO TABLE logs" );

    resultSet = statement.executeQuery( "SELECT * FROM logs" );

    while ( resultSet.next() ) {
      System.out.println( resultSet.getString( 1 ) + " --- " + resultSet.getString( 2 ) );
    }

    resultSet.close();
    statement.close();
    connection.close();

Exampes use 'log.txt' with content:

    2012-02-03 18:35:34 SampleClass6 [INFO] everything normal for id 577725851
    2012-02-03 18:35:34 SampleClass4 [FATAL] system problem at id 1991281254
    2012-02-03 18:35:34 SampleClass3 [DEBUG] detail for id 1304807656
    2012-02-03 18:35:34 SampleClass3 [WARN] missing id 423340895
    2012-02-03 18:35:34 SampleClass5 [TRACE] verbose detail for id 2082654978
    2012-02-03 18:35:34 SampleClass0 [ERROR] incorrect id  1886438513
    2012-02-03 18:35:34 SampleClass9 [TRACE] verbose detail for id 438634209
    2012-02-03 18:35:34 SampleClass8 [DEBUG] detail for id 2074121310
    2012-02-03 18:35:34 SampleClass0 [TRACE] verbose detail for id 1505582508
    2012-02-03 18:35:34 SampleClass0 [TRACE] verbose detail for id 1903854437
    2012-02-03 18:35:34 SampleClass7 [DEBUG] detail for id 915853141
    2012-02-03 18:35:34 SampleClass3 [TRACE] verbose detail for id 303132401
    2012-02-03 18:35:34 SampleClass6 [TRACE] verbose detail for id 151914369
    2012-02-03 18:35:34 SampleClass2 [DEBUG] detail for id 146527742
    ...

Expected output:

    2012-02-03 --- 18:35:34 --- SampleClass6 --- [INFO]
    2012-02-03 --- 18:35:34 --- SampleClass4 --- [FATAL]
    2012-02-03 --- 18:35:34 --- SampleClass3 --- [DEBUG]
    2012-02-03 --- 18:35:34 --- SampleClass3 --- [WARN]
    2012-02-03 --- 18:35:34 --- SampleClass5 --- [TRACE]
    2012-02-03 --- 18:35:34 --- SampleClass0 --- [ERROR]
    2012-02-03 --- 18:35:34 --- SampleClass9 --- [TRACE]
    2012-02-03 --- 18:35:34 --- SampleClass8 --- [DEBUG]
    2012-02-03 --- 18:35:34 --- SampleClass0 --- [TRACE]
    2012-02-03 --- 18:35:34 --- SampleClass0 --- [TRACE]
    2012-02-03 --- 18:35:34 --- SampleClass7 --- [DEBUG]
    2012-02-03 --- 18:35:34 --- SampleClass3 --- [TRACE]
    2012-02-03 --- 18:35:34 --- SampleClass6 --- [TRACE]
    2012-02-03 --- 18:35:34 --- SampleClass2 --- [DEBUG]
    ...
