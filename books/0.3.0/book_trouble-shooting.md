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

{{Trouble Shooting}}
--------------------

### Enabling Logging ###

The `log4j.properties` files `<GATEWAY_HOME>/conf` can be used to change the granularity of the logging done by Knox. &nbsp;The Knox server must be restarted in order for these changes to take effect.
There are various useful loggers pre-populated in that file but they are commented out.

    log4j.logger.org.apache.hadoop.gateway=DEBUG # Use this logger to increase the debugging of Apache Knox itself.
    log4j.logger.org.apache.shiro=DEBUG          # Use this logger to increase the debugging of Apache Shiro.
    log4j.logger.org.apache.http=DEBUG           # Use this logger to increase the debugging of Apache HTTP components.
    log4j.logger.org.apache.http.client=DEBUG    # Use this logger to increase the debugging of Apache HTTP client component.
    log4j.logger.org.apache.http.headers=DEBUG   # Use this logger to increase the debugging of Apache HTTP header.
    log4j.logger.org.apache.http.wire=DEBUG      # Use this logger to increase the debugging of Apache HTTP wire traffic.

### Filing Bugs ###

h2. Filing bugs

Bugs can be filed using [Jira](https://issues.apache.org/jira/browse/KNOX).
Please include the results of this command below in the Environment section.
Also include the version of Hadoop being used.

    java -jar bin/server.jar -version

