package com.example.qj.demo.controller;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.*;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.KieServices;
import org.kie.internal.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/coreEgnine")
public class CoreEngineServiceController {
	
	@Autowired
	private DeploymentService deploymentService;
	@Autowired
	private RuntimeDataService runtimeDataService;
	@Autowired
	private DefinitionService definitionService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private UserTaskService userTaskService;

	/**
	 * 测试核心引擎的service的使用
	 * @return
	 */
    @RequestMapping("/")
    public String index() {
    	String res="";
    	try {
			Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
			Collection<ProcessDefinition> processDefinitions = runtimeDataService.getProcesses(new QueryContext(0, 100));
			Map<String,String> map=definitionService.getProcessVariables("x","y");
			if(processService!=null && userTaskService!=null){
				System.out.println("核心引擎服务被成功注入");
			}

			res="core engine services inject success!";
		}catch (Exception e){
			res="core engine services inject failed!";
		}
        return res;
    }


	/**
	 * 测试自动生成的37张表的使用
	 * 1.部署默认的流程模块--会在DeploymentStore,sessioninfo添加一条记录。程序会将指定位置的bpmn文件存放到该模块中。
	 * 2.从默认流程模块中选择一个流程定义规则创建流程实例：
	 * 		会在NodeInstanceLog表中添加该流程的3个节点信息，会在ProcessInstanceInfo表中添加一条记录，并在ProcessInstanceLog表中添加一条操作日志
	 * 		在VariableInstanceLog表中添加8条记录，因为流程中有humanTask，所以在workiteminfo表中会添加一条记录，CorrelationKeyInfo和CorrelationPropertyInfo表中添加一条记录
	 * @return
	 */
	@RequestMapping("/simple")
    public String simpleBusiness(String deploymentId,String processId){

		String res="";

		//1.加载默认模块单元，找到指定位置已经加载进去的bpmn文件
		if(deploymentId==null || "".equals(deploymentId)){
			deploymentId ="org.jbpm:helloworld:1.0";
			processId="hello";
			res="default bpmn is deployment!";
		}else if(processId==null || "".equals(deploymentId)){
			res="processId is null";
		}else{
			res="new process is deployed";
		}
		String[] gav = deploymentId.split(":");
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2], null, null, "SINGLETON");
		if(!deploymentService.isDeployed(deploymentId)){
			deploymentService.deploy(unit);
		}
		//2.选择其中1个流程规则，创建流程实例.并存储业务有关的比如用户信息等
		long processInstanceId = processService.startProcess(deploymentId, processId, (Map<String, Object>) null);

    	return res;
	}

	/**
	 * 卸载已经部署的版本
	 * id的含义是deploymentUnitId--部署单元的ID,也就是部署ID
	 * @RequestParam注解作用是该参数必须填写
	 * @param deploymentUnitId
	 * @return
	 */
	@RequestMapping("/unDeploy")
	public String unDeploy(String deploymentUnitId){

		String res="";
		if(deploymentUnitId==null || "".equals(deploymentUnitId)){
			deploymentUnitId="org.jbpm:helloworld:1.0";
		}

		DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnitId);
		if (deployed != null) {
			deploymentService.undeploy(deployed.getDeploymentUnit());
			res = "Deployment " + deploymentUnitId + " undeployed successfully";
		} else {
			res = "No deployment " + deploymentUnitId + " found";
		}

		return res;
	}

	/**
	 * 手动打包版本，并部署操作
	 * @return
	 */
	@RequestMapping("/manualDeploy")
	public String manualDeploy(String deploymentId,String processId,String... bpmn){

		if(deploymentId==null || "".equals(deploymentId)){
			deploymentId="org.jbpm:helloworld:2.0";
		}
		if (bpmn==null|| bpmn.length==0){
			bpmn=new String[]{"rule/hello.bpmn"};
		}
		KieBaseAndKieSessionFactory.reloadKieContainer(deploymentId,bpmn);
		String res=simpleBusiness(deploymentId,processId);
		return res;
	}



}
