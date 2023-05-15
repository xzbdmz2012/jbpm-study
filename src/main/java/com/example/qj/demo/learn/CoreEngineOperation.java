package com.example.qj.demo.learn;

import com.example.qj.demo.constant.ProcessConstant;
import com.example.qj.demo.util.CoreEngineServiceFactory;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceQueryMapper;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.*;
import org.jbpm.services.api.query.NamedQueryMapper;
import org.jbpm.services.api.query.QueryService;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.*;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.manager.context.EmptyContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置和创建引擎core Service 的服务，关键是持久化中的JNDI配置
 */
public class CoreEngineOperation{

    /**
     * 0.Runtime Manager
     * 测试核心引擎的runtimeManager的使用，创建session
     * 应用场景：应用开始；在请求期间；应用结束时。因为能够获取session和taskservice。
     * 需要导入的包jbpm-kie-services
     */
    @Test
    public void runtimeManagerTest(){

        //1.创建运行时环境
        RuntimeEnvironment environment= RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultInMemoryBuilder()
                .addAsset(ResourceFactory.newClassPathResource("rule/hello.bpmn"), ResourceType.BPMN2)
                .get();

        //2.创建运行时管理器
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);

        //3.创建运行时引擎
        RuntimeEngine runtimeEngine = manager.getRuntimeEngine(EmptyContext.get());

        //4.创建session
        KieSession ksession = runtimeEngine.getKieSession();
        manager.disposeRuntimeEngine(runtimeEngine);

        System.out.println("runtimeManager 测试完成！");
    }


    /**
     * 1.Deployment Service
     * 测试核心引擎API中部署服务的使用
     * 应用场景：为系统提供动态行为，让多个kjar可以同时激活并同时执行
     * kmodule存储在maven仓库中，URL地址为http://repo1.maven.org/maven2/
     * https://repo.maven.apache.org:443
     * TODO
     */
    @Test
    public void deploymentServiceTest() throws Exception {

        //1.通过打包的版本信息，获取部署单元
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ProcessConstant.GROUP_ID, ProcessConstant.ARTIFACT_ID, ProcessConstant.VERSION);
        /**
         * 2. 部署服务执行，部署具体的流程单元.----最关键的是DeploymentService对象的创建过程
         * TODO DeploymentService对象很多的配置操作，目前还没有梳理清楚......
         */
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnit);

        //3.检索部署单元
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        //4.获取运行时管理器具
        RuntimeManager manager = deployed.getRuntimeManager();
    }


    /**
     * 2.Definition Service
     * 测试核心引擎API中定义服务的使用
     * TODO
     */
    @Test
    public void definitionServiceTest() throws Exception {

        //1.创建部署单元
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ProcessConstant.GROUP_ID, ProcessConstant.ARTIFACT_ID, ProcessConstant.VERSION);

        //2.部署服务执行
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnit);

        //从定义服务获取各种信息，包括流程变量，任务名等
        String processId = "hello";
        Map<String, String> processData = CoreEngineServiceFactory.serviceConfigurator.getBpmn2Service().getProcessVariables(deploymentUnit.getIdentifier(), processId);
    }


    /**
     * 3.Process Service
     * 测试核心引擎API中Process服务的使用
     * 可以执行Command对象。可以访问执行环境。专注于运行时操作
     * TODO
     */
    @Test
    public void processServiceTest() throws Exception {

        //1.创建部署单元
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ProcessConstant.GROUP_ID, ProcessConstant.ARTIFACT_ID, ProcessConstant.VERSION);

        //2.部署服务执行
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnit);

        //3.开始流程
        ProcessService processService=CoreEngineServiceFactory.serviceConfigurator.getProcessService();
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask");
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    }


    /**
     * 4.Runtime Data Service
     * 测试核心引擎API中运行时数据服务的使用
     * 处理所有的运行时信息
     * TODO
     */
    @Test
    public void runtimeDataServiceTest() throws Exception {
        //1.创建部署单元
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ProcessConstant.GROUP_ID, ProcessConstant.ARTIFACT_ID, ProcessConstant.VERSION);

        //2.部署服务执行
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnit);

        //3.进行流程运行时的各种操作--主要参数是查询上下文
        RuntimeDataService runtimeDataService=CoreEngineServiceFactory.serviceConfigurator.getRuntimeDataService();
        Collection definitions = runtimeDataService.getProcesses(new QueryContext());
        Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(new QueryContext());
        long processInstanceId=1;
        Collection<NodeInstanceDesc> nodeInstances = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter(0, 10));

    }


    /**
     * 5.User Task Service
     * 测试核心引擎API中用户任务服务的使用
     * 覆盖完整的额任务的生命周期
     * TODO
     */
    @Test
    public void userTaskServiceTest() throws Exception {

        //1.创建部署单元
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ProcessConstant.GROUP_ID, ProcessConstant.ARTIFACT_ID, ProcessConstant.VERSION);

        //2.部署服务执行
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnit);


        //3.获取用户任务，声明任务，然后传入参数执行任务
        long processInstanceId = CoreEngineServiceFactory.serviceConfigurator.getProcessService().startProcess(deploymentUnit.getIdentifier(), "hello");
        List<Long> taskIds = CoreEngineServiceFactory.serviceConfigurator.getRuntimeDataService().getTasksByProcessInstanceId(processInstanceId);
        Long taskId = taskIds.get(0);

        UserTaskService userTaskService=CoreEngineServiceFactory.serviceConfigurator.getUserTaskService();
        userTaskService.start(taskId, "john");
        UserTaskInstanceDesc task = CoreEngineServiceFactory.serviceConfigurator.getRuntimeDataService().getTaskById(taskId);

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        userTaskService.complete(taskId, "john", results);
    }


    /**
     * 6.Quartz-based Timer Service
     * 测试核心引擎API中基于Quartz的定时器服务的使用
     * 能够在任何时候加载KIE Session
     * 目前只有配置文件
     * TODO
     */
    @Test
    public void quartzBasedTimerServiceTest(){
        /**
         * 配置文件的内容主要有：
         * 1.Configure Main Scheduler Properties
         * 2.Configure ThreadPool
         * 3.Configure JobStore
         * 4.Other Example Delegates
         * 5.Configure Datasources
         */

    }


    /**
     * 7.Query Service
     * 测试核心引擎API中查询服务的使用
     * 提供基于Dashbuilder数据集合的高级搜索，支持多种数据源CSV, SQL, elastic search等
     * 它是整个查询功能的一部分
     * TODO
     */
    @Test
    public void queryServiceTest() throws Exception {

        //1.创建部署单元
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ProcessConstant.GROUP_ID, ProcessConstant.ARTIFACT_ID, ProcessConstant.VERSION);

        //2.部署服务执行
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnit);

        //3.查询流程记录,可以设置专门的过滤对象
        QueryService queryService=CoreEngineServiceFactory.serviceConfigurator.getQueryService();
        queryService.query("my query def", new NamedQueryMapper<Collection<ProcessInstanceDesc>>("ProcessInstances"), new QueryContext());
        queryService.query("my query def", ProcessInstanceQueryMapper.get(), new QueryContext(), ()->{
                                                                                                            ColumnFilter filter = FilterFactory.OR(
                                                                                                                        FilterFactory.greaterOrEqualsTo(1),
                                                                                                                        FilterFactory.lowerOrEqualsTo( (100))
                                                                                                                                                  );
                                                                                                            String columnName = "processInstanceId";
                                                                                                            filter.setColumnId(columnName);
                                                                                                            return filter;
                                                                                                         }
                          );

        //4.典型使用场景--自定义查询，然后注册并执行
        SqlQueryDefinition query = new SqlQueryDefinition("getAllProcessInstances", "java:jboss/datasources/ExampleDS");
        query.setExpression("select * from processinstancelog");
        queryService.registerQuery(query);

        QueryContext ctx = new QueryContext(0, 100, "start_date", true);
        Collection<ProcessInstanceDesc> instances = queryService.query("getAllProcessInstances", ProcessInstanceQueryMapper.get(), ctx);
    }


    /**
     * 8.ProcessInstance Migratio Service
     * 测试核心引擎API中流程实例迁移服务的使用
     * 迁移过程不影响任务变量。操作是结束旧流程，开启新流程
     * 流程最后是向后兼容
     * TODO
     */
    @Test
    public void processInstanceMigratioServiceTest(){

    }


    /**
     * 9.Working with deployments
     * 测试核心引擎API中与部署相关工作
     * 一般部署最新版的流程
     * TODO
     */
    @Test
    public void workWithDeploymentTest() throws Exception {

        KModuleDeploymentUnit deploymentUnitV1 = new KModuleDeploymentUnit("org.jbpm", "HR", "1.0");
        //2.部署服务执行
        CoreEngineServiceFactory.init();
        DeploymentService deploymentService= CoreEngineServiceFactory.serviceConfigurator.getDeploymentService();
        deploymentService.deploy(deploymentUnitV1);

        ProcessService processService=CoreEngineServiceFactory.serviceConfigurator.getProcessService();
        long processInstanceId = processService.startProcess("org.jbpm:HR:LATEST", "customtask");

        //runtimeDataService使用获取流程信息
        RuntimeDataService runtimeDataService=CoreEngineServiceFactory.serviceConfigurator.getRuntimeDataService();
        ProcessInstanceDesc piDesc = runtimeDataService.getProcessInstanceById(processInstanceId);


        KModuleDeploymentUnit deploymentUnitV2 = new KModuleDeploymentUnit("org.jbpm", "HR", "2.0");
        deploymentService.deploy(deploymentUnitV2);

        //processService使用启动流程
        processInstanceId = processService.startProcess("org.jbpm:HR:LATEST", "customtask");
        piDesc = runtimeDataService.getProcessInstanceById(processInstanceId);

    }


































}
