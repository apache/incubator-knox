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

### HBase ###

TODO

#### HBase URL Mapping ####

TODO

#### HBase Examples ####

TODO

The examples below illustrate the set of basic operations with HBase instance using Stargate REST API.
Use following link to get more more details about HBase/Stargate API: http://wiki.apache.org/hadoop/Hbase/Stargate.

### Assumptions ###

This document assumes a few things about your environment in order to simplify the examples.

1. The JVM is executable as simply java.
2. The Apache Knox Gateway is installed and functional.
3. The example commands are executed within the context of the GATEWAY_HOME current directory.  The GATEWAY_HOME directory is the directory within the Apache Knox Gateway installation that contains the README file and the bin, conf and deployments directories.
4. A few examples optionally require the use of commands from a standard Groovy installation.  These examples are optional but to try them you will need Groovy [installed|http://groovy.codehaus.org/Installing+Groovy].

### HBase Stargate Setup ###

#### Launch Stargate ####
The command below launches the Stargate daemon on port 60080

    sudo /usr/lib/hbase/bin/hbase-daemon.sh start rest -p 60080

60080 post is used because it was specified in sample Hadoop cluster deployment {{\{GATEWAY_HOME\}}}/deployments/sample.xml.

#### Configure Sandbox port mapping for VirtualBox

1. Select the VM
2. Select menu Machine>Settings...
3. Select tab Network
4. Select Adapter 1
5. Press Port Forwarding button
6. Press Plus button to insert new rule: Name=Stargate, Host Port=60080, Guest Port=60080
7. Press OK to close the rule window
8. Press OK to Network window save the changes

60080 post is used because it was specified in sample Hadoop cluster deployment {{\{GATEWAY_HOME\}}}/deployments/sample.xml.

### HBase/Stargate via KnoxShell DSL

#### Usage
For more details about client DSL usage please follow this [page|https://cwiki.apache.org/confluence/display/KNOX/Client+Usage].
 
##### systemVersion() - Query Software Version.

* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).systemVersion().now().string}}

##### clusterVersion() - Query Storage Cluster Version.

* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).clusterVersion().now().string}}

##### status() - Query Storage Cluster Status.

* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).status().now().string}}

##### table().list() - Query Table List.

* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
  * {{HBase.session(session).table().list().now().string}}

##### table(String tableName).schema() - Query Table Schema.

* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).table().schema().now().string}}

##### table(String tableName).create() - Create Table Schema.
* Request
    * attribute(String name, Object value) - the table's attribute.
    * family(String name) - starts family definition. Has sub requests:
    * attribute(String name, Object value) - the family's attribute.
    * endFamilyDef() - finishes family definition.
* Response
    * EmptyResponse
* Example
    * {{HBase.session(session).table(tableName).create()}}
     {{.attribute("tb_attr1", "value1")}}
     {{.attribute("tb_attr2", "value2")}}
     {{.family("family1")}}
         {{.attribute("fm_attr1", "value3")}}
         {{.attribute("fm_attr2", "value4")}}
     {{.endFamilyDef()}}
     {{.family("family2")}}
     {{.family("family3")}}
     {{.endFamilyDef()}}
     {{.attribute("tb_attr3", "value5")}}
     {{.now()}}

##### table(String tableName).update() - Update Table Schema.
* Request
    * family(String name) - starts family definition. Has sub requests:
    * attribute(String name, Object value) - the family's attribute.
    * endFamilyDef() - finishes family definition.
* Response
    * EmptyResponse
* Example
    * {{HBase.session(session).table(tableName).update()}}
     {{.family("family1")}}
         {{.attribute("fm_attr1", "new_value3")}}
     {{.endFamilyDef()}}
     {{.family("family4")}}
         {{.attribute("fm_attr3", "value6")}}
     {{.endFamilyDef()}}
     {{.now()}}

##### table(String tableName).regions() - Query Table Metadata.
* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).table(tableName).regions().now().string}}

##### table(String tableName).delete() - Delete Table.
* Request
    * No request parameters.
* Response
    * EmptyResponse
* Example
    * {{HBase.session(session).table(tableName).delete().now()}}

##### table(String tableName).row(String rowId).store() - Cell Store.
* Request
    * column(String family, String qualifier, Object value, Long time) - the data to store; "qualifier" may be "null"; "time" is optional.
* Response
    * EmptyResponse
* Example
    * {{HBase.session(session).table(tableName).row("row_id_1").store()}}
     {{.column("family1", "col1", "col_value1")}}
     {{.column("family1", "col2", "col_value2", 1234567890l)}}
     {{.column("family2", null, "fam_value1")}}
     {{.now()}}
    * {{HBase.session(session).table(tableName).row("row_id_2").store()}}
     {{.column("family1", "row2_col1", "row2_col_value1")}}
     {{.now()}}

##### table(String tableName).row(String rowId).query() - Cell or Row Query.
* rowId is optional. Querying with null or empty rowId will select all rows.
* Request
    * column(String family, String qualifier) - the column to select; "qualifier" is optional.
    * startTime(Long) - the lower bound for filtration by time.
    * endTime(Long) - the upper bound for filtration by time.
    * times(Long startTime, Long endTime) - the lower and upper bounds for filtration by time.
    * numVersions(Long) - the maximum number of versions to return.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).table(tableName).row("row_id_1")}}
     {{.query()}}
     {{.now().string}}
    * {{HBase.session(session).table(tableName).row().query().now().string}}
    * {{HBase.session(session).table(tableName).row().query()}}
     {{.column("family1", "row2_col1")}}
     {{.column("family2")}}
     {{.times(0, Long.MAX_VALUE)}}
     {{.numVersions(1)}}
     {{.now().string}}

##### table(String tableName).row(String rowId).delete() - Row, Column, or Cell Delete.
* Request
    * column(String family, String qualifier) - the column to delete; "qualifier" is optional.
    * time(Long) - the upper bound for time filtration.
* Response
    * EmptyResponse
* Example
    * {{HBase.session(session).table(tableName).row("row_id_1")}}
     {{.delete()}}
     {{.column("family1", "col1")}}
     {{.now()}}
    * {{HBase.session(session).table(tableName).row("row_id_1")}}
     {{.delete()}}
     {{.column("family2")}}
     {{.time(Long.MAX_VALUE)}}
     {{.now()}}

##### table(String tableName).scanner().create() - Scanner Creation.
* Request
    * startRow(String) - the lower bound for filtration by row id.
    * endRow(String) - the upper bound for filtration by row id.
    * rows(String startRow, String endRow) - the lower and upper bounds for filtration by row id.
    * column(String family, String qualifier) - the column to select; "qualifier" is optional.
    * batch(Integer) - the batch size.
    * startTime(Long) - the lower bound for filtration by time.
    * endTime(Long) - the upper bound for filtration by time.
    * times(Long startTime, Long endTime) - the lower and upper bounds for filtration by time.
    * filter(String) - the filter XML definition.
    * maxVersions(Integer) - the the maximum number of versions to return.
* Response
    * scannerId : String - the scanner ID of the created scanner. Consumes body.
* Example
    * {{HBase.session(session).table(tableName).scanner().create()}}
     {{.column("family1", "col2")}}
     {{.column("family2")}}
     {{.startRow("row_id_1")}}
     {{.endRow("row_id_2")}}
     {{.batch(1)}}
     {{.startTime(0)}}
     {{.endTime(Long.MAX_VALUE)}}
     {{.filter("")}}
     {{.maxVersions(100)}}
     {{.now()}}

##### table(String tableName).scanner(String scannerId).getNext() - Scanner Get Next.
* Request
    * No request parameters.
* Response
    * BasicResponse
* Example
    * {{HBase.session(session).table(tableName).scanner(scannerId).getNext().now().string}}

##### table(String tableName).scanner(String scannerId).delete() - Scanner Deletion.
* Request
    * No request parameters.
* Response
    * EmptyResponse
* Example
    * {{HBase.session(session).table(tableName).scanner(scannerId).delete().now()}}

#### Examples

This example illustrates sequence of all basic HBase operations: 
1. get system version
2. get cluster version
3. get cluster status
4. create the table
5. get list of tables
6. get table schema
7. update table schema
8. insert single row into table
9. query row by id
10. query all rows
11. delete cell from row
12. delete entire column family from row
13. get table regions
14. create scanner
15. fetch values using scanner
16. drop scanner
17. drop the table

There are several ways to do this depending upon your preference.

You can use the Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar samples/ExampleHBaseUseCase.groovy

You can manually type in the KnoxShell DSL script into the interactive Groovy interpreter provided with the distribution.

    java -jar bin/shell.jar

Each line from the file below will need to be typed or copied into the interactive shell.

{code:title="samples/ExampleHBaseUseCase.groovy"}

    /**
     * Licensed to the Apache Software Foundation (ASF) under one
     * or more contributor license agreements.  See the NOTICE file
     * distributed with this work for additional information
     * regarding copyright ownership.  The ASF licenses this file
     * to you under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance
     * with the License.  You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    package org.apache.hadoop.gateway.shell.hbase

    import org.apache.hadoop.gateway.shell.Hadoop

    import static java.util.concurrent.TimeUnit.SECONDS

    gateway = "https://localhost:8443/gateway/sandbox"
    username = "guest"
    password = "guest-password"
    tableName = "test_table"

    session = Hadoop.login(gateway, username, password)

    println "System version : " + HBase.session(session).systemVersion().now().string

    println "Cluster version : " + HBase.session(session).clusterVersion().now().string

    println "Status : " + HBase.session(session).status().now().string

    println "Creating table '" + tableName + "'..."

    HBase.session(session).table(tableName).create()  \
        .attribute("tb_attr1", "value1")  \
        .attribute("tb_attr2", "value2")  \
        .family("family1")  \
            .attribute("fm_attr1", "value3")  \
            .attribute("fm_attr2", "value4")  \
        .endFamilyDef()  \
        .family("family2")  \
        .family("family3")  \
        .endFamilyDef()  \
        .attribute("tb_attr3", "value5")  \
        .now()

    println "Done"

    println "Table List : " + HBase.session(session).table().list().now().string

    println "Schema for table '" + tableName + "' : " + HBase.session(session)  \
        .table(tableName)  \
        .schema()  \
        .now().string

    println "Updating schema of table '" + tableName + "'..."

    HBase.session(session).table(tableName).update()  \
        .family("family1")  \
            .attribute("fm_attr1", "new_value3")  \
        .endFamilyDef()  \
        .family("family4")  \
            .attribute("fm_attr3", "value6")  \
        .endFamilyDef()  \
        .now()

    println "Done"

    println "Schema for table '" + tableName + "' : " + HBase.session(session)  \
        .table(tableName)  \
        .schema()  \
        .now().string

    println "Inserting data into table..."

    HBase.session(session).table(tableName).row("row_id_1").store()  \
        .column("family1", "col1", "col_value1")  \
        .column("family1", "col2", "col_value2", 1234567890l)  \
        .column("family2", null, "fam_value1")  \
        .now()

    HBase.session(session).table(tableName).row("row_id_2").store()  \
        .column("family1", "row2_col1", "row2_col_value1")  \
        .now()

    println "Done"

    println "Querying row by id..."

    println HBase.session(session).table(tableName).row("row_id_1")  \
        .query()  \
        .now().string

    println "Querying all rows..."

    println HBase.session(session).table(tableName).row().query().now().string

    println "Querying row by id with extended settings..."

    println HBase.session(session).table(tableName).row().query()  \
        .column("family1", "row2_col1")  \
        .column("family2")  \
        .times(0, Long.MAX_VALUE)  \
        .numVersions(1)  \
        .now().string

    println "Deleting cell..."

    HBase.session(session).table(tableName).row("row_id_1")  \
        .delete()  \
        .column("family1", "col1")  \
        .now()

    println "Rows after delete:"

    println HBase.session(session).table(tableName).row().query().now().string

    println "Extended cell delete"

    HBase.session(session).table(tableName).row("row_id_1")  \
        .delete()  \
        .column("family2")  \
        .time(Long.MAX_VALUE)  \
        .now()

    println "Rows after delete:"

    println HBase.session(session).table(tableName).row().query().now().string

    println "Table regions : " + HBase.session(session).table(tableName)  \
        .regions()  \
        .now().string

    println "Creating scanner..."

    scannerId = HBase.session(session).table(tableName).scanner().create()  \
        .column("family1", "col2")  \
        .column("family2")  \
        .startRow("row_id_1")  \
        .endRow("row_id_2")  \
        .batch(1)  \
        .startTime(0)  \
        .endTime(Long.MAX_VALUE)  \
        .filter("")  \
        .maxVersions(100)  \
        .now().scannerId

    println "Scanner id=" + scannerId

    println "Scanner get next..."

    println HBase.session(session).table(tableName).scanner(scannerId)  \
        .getNext()  \
        .now().string

    println "Dropping scanner with id=" + scannerId

    HBase.session(session).table(tableName).scanner(scannerId).delete().now()

    println "Done"

    println "Dropping table '" + tableName + "'..."

    HBase.session(session).table(tableName).delete().now()

    println "Done"

    session.shutdown(10, SECONDS)

### HBase/Stargate via cURL

#### Get software version

Set Accept Header to "text/plain", "text/xml", "application/json" or "application/x-protobuf"

    %  curl -ik -u guest:guest-password\
     -H "Accept:  application/json"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/version'

#### Get version information regarding the HBase cluster backing the Stargate instance

Set Accept Header to "text/plain", "text/xml" or "application/x-protobuf"

    %  curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/version/cluster'

#### Get detailed status on the HBase cluster backing the Stargate instance.

Set Accept Header to "text/plain", "text/xml", "application/json" or "application/x-protobuf"

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/status/cluster'

#### Get the list of available tables.

Set Accept Header to "text/plain", "text/xml", "application/json" or "application/x-protobuf"

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase'

#### Create table with two column families using xml input

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"   -H "Content-Type: text/xml"\
     -d '<?xml version="1.0" encoding="UTF-8"?><TableSchema name="table1"><ColumnSchema name="family1"/><ColumnSchema name="family2"/></TableSchema>'\
     -X PUT 'https://localhost:8443/gateway/sandbox/hbase/table1/schema'

#### Create table with two column families using JSON input

    % curl -ik -u guest:guest-password\
     -H "Accept: application/json"  -H "Content-Type: application/json"\
     -d '{"name":"table2","ColumnSchema":[{"name":"family3"},{"name":"family4"}]}'\
     -X PUT 'https://localhost:8443/gateway/sandbox/hbase/table2/schema'

#### Get table metadata

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/table1/regions'

#### Insert single row table

    % curl -ik -u guest:guest-password\
     -H "Content-Type: text/xml"\
     -H "Accept: text/xml"\
     -d '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><CellSet><Row key="cm93MQ=="><Cell column="ZmFtaWx5MTpjb2wx" >dGVzdA==</Cell></Row></CellSet>'\
     -X POST 'https://localhost:8443/gateway/sandbox/hbase/table1/row1'

#### Insert multiple rows into table

    % curl -ik -u guest:guest-password\
     -H "Content-Type: text/xml"\
     -H "Accept: text/xml"\
     -d '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><CellSet><Row key="cm93MA=="><Cell column=" ZmFtaWx5Mzpjb2x1bW4x" >dGVzdA==</Cell></Row><Row key="cm93MQ=="><Cell column=" ZmFtaWx5NDpjb2x1bW4x" >dGVzdA==</Cell></Row></CellSet>'\
     -X POST 'https://localhost:8443/gateway/sandbox/hbase/table2/false-row-key'

#### Get all data from table

Set Accept Header to "text/plain", "text/xml", "application/json" or "application/x-protobuf"

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/table1/*'

#### Execute cell or row query

Set Accept Header to "text/plain", "text/xml", "application/json" or "application/x-protobuf"

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/table1/row1/family1:col1'

#### Delete entire row from table

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X DELETE 'https://localhost:8443/gateway/sandbox/hbase/table2/row0'

#### Delete column family from row

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X DELETE 'https://localhost:8443/gateway/sandbox/hbase/table2/row0/family3'

#### Delete specific column from row

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X DELETE 'https://localhost:8443/gateway/sandbox/hbase/table2/row0/family3'

#### Create scanner

Scanner URL will be in Location response header

    % curl -ik -u guest:guest-password\
     -H "Content-Type: text/xml"\
     -d '<Scanner batch="1"/>'\
     -X PUT 'https://localhost:8443/gateway/sandbox/hbase/table1/scanner'

#### Get the values of the next cells found by the scanner

    % curl -ik -u guest:guest-password\
     -H "Accept: application/json"\
     -X GET 'https://localhost:8443/gateway/sandbox/hbase/table1/scanner/13705290446328cff5ed'

#### Delete scanner

    % curl -ik -u guest:guest-password\
     -H "Accept: text/xml"\
     -X DELETE 'https://localhost:8443/gateway/sandbox/hbase/table1/scanner/13705290446328cff5ed'

#### Delete table

    % curl -ik -u guest:guest-password\
     -X DELETE 'https://localhost:8443/gateway/sandbox/hbase/table1/schema'
