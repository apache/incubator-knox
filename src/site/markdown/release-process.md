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
-->

Release Process
---------------
The Apache Knox Gateway is currently distributed as a single Zip file.
Following the steps described below this Zip file will be created in the target directory.
The following process can be used create a release build without an existing repository clone.

    git clone https://git-wip-us.apache.org/repos/asf/incubator-knox.git knox
    cd knox
    ant release

If the repository has already been cloned the following process should be used.

    git pull
    ant release


Disclaimer
----------
The Apache Knox Gateway is an effort undergoing incubation at the
Apache Software Foundation (ASF), sponsored by the Apache Incubator PMC.

Incubation is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process
have stabilized in a manner consistent with other successful ASF projects.

While incubation status is not necessarily a reflection of the completeness
or stability of the code, it does indicate that the project has yet to be
fully endorsed by the ASF.