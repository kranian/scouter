/*
 *  Copyright 2015 the original author or authors.
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package scouterx.webapp.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import scouterx.webapp.framework.client.server.ServerManager;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
* Request DTO for history alert
*
* * @author yosong.heo
*/
@Getter
@Setter
@ToString
@Slf4j
public class LoadTimeAlertRequest {
    int serverId;

    @Min(0)
    @PathParam("offset1")
    long loop;

    @Min(0)
    @PathParam("offset2")
    int index;

    @NotNull
    @PathParam("yyyymmdd")
    String yyyymmdd;

    @QueryParam("level")
    String level;

    @QueryParam("startTimeMillis")
    long startTimeMillis;

    @QueryParam("endTimeMillis")
    long endTimeMillis;

    @QueryParam("limit")
    long count;
    @QueryParam("object")
    String objectName;

    @QueryParam("keyword")
    String keyword;

    @QueryParam("serverId")
    public void setServerId(int serverId) {
        this.serverId = ServerManager.getInstance().getServerIfNullDefault(serverId).getId();
    }

}
