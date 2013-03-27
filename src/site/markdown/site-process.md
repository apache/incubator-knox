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

Apache Site Publication Process
-------------------------------
The following process can be used to publish the generated site to Apache without an existing repository clone.

    git clone https://git-wip-us.apache.org/repos/asf/incubator-knox.git knox
    cd knox
    ant publish

If the repository has already been cloned the following process should be used.

    git pull
    ant publish

Unfortunately there are two know issues with this process.

1.  All files in the site are updated in Subversion even if they have not change.
    As the site becomes larger and contains more images this may become a serious issue.

2.  Any renamed or removed files will result in "orphaned" files in Subversion.
    These will need to be cleaned up explicitly.





