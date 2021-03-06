/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.jobexecutor;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.StartProcessInstanceCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TimerStartEventJobHandler implements JobHandler {

  private static Logger log = LoggerFactory.getLogger(TimerStartEventJobHandler.class);

  public static final String TYPE = "timer-start-event";

  public String getType() {
    return TYPE;
  }
  
  public void execute(JobEntity job, String configuration, ExecutionEntity execution, CommandContext commandContext) {
    DeploymentManager deploymentCache = Context
            .getProcessEngineConfiguration()
            .getDeploymentManager();
    
    ProcessDefinition processDefinition = deploymentCache.findDeployedLatestProcessDefinitionByKey(configuration);
    try {
      if(!processDefinition.isSuspended()) {
        new StartProcessInstanceCmd(configuration, null, null, null).execute(commandContext);
      } else {
        log.debug("ignoring timer of suspended process definition {}", processDefinition.getName());
      }
    } catch (RuntimeException e) {
      log.error("exception during timer execution", e);
      throw e;
    } catch (Exception e) {
      log.error("exception during timer execution", e);
      throw new ActivitiException("exception during timer execution: " + e.getMessage(), e);
    }
  }
}
