package com.example.qj.demo.learn;

import com.example.qj.demo.handler.CalcWorkItemHandler;
import com.example.qj.demo.handler.MyWorkItemHandler;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.manager.*;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalculateNewOpeation extends JbpmJUnitBaseTestCase {

    @Autowired
    private MyWorkItemHandler workItemHandler;
    @Autowired
    private CalcWorkItemHandler calcWorkItemHandler;

    /**
     * 关键，启动任务的配置
     */
    public CalculateNewOpeation(){
        super(true,true);
    }

    /**
     * 流程中进行节点之间的数据传递方式测试
     * 只是数据传入，然后在下一个节点打印，中间没有其他操作
     */
    @Test
    public void dataTransferTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("dataTransfer.bpmn2");
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);

        MyWorkItemHandler workItemHandler = new MyWorkItemHandler();
        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance=kieSession.startProcess("dataTransfer");

        /**
         * 如果这里没有执行kieSession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), res);
         * 主程序依旧会正常结束，但是流程并没有跑完。流程结束的最终状态应该为ProcessInstance.STATE_COMPLETED
         */
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("pass", "hello,world");
        System.out.println(workItemHandler.toString());
        kieSession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), res);

        //验证usertask是否执行完成
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    /**
     * 最新版的a+b流程。时间2018年12月19日
     * @throws Exception
     */
    @Test
    public void calculateAAndBTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("simple.bpmn2");
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);

        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kieSession.getWorkItemManager().registerWorkItemHandler("Service Task",calcWorkItemHandler);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)kieSession.startProcess("simple");

        /**
         * 如果这里没有执行kieSession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), res);
         * 主程序依旧会正常结束，但是流程并没有跑完。流程结束的最终状态应该为ProcessInstance.STATE_COMPLETED
         */
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("a", Math.random());
        res.put("b", Math.random());
//        res.put("b", "hello，world");
        System.out.println("userTask: "+workItemHandler.getWorkItem());
        System.out.println("serviceTask: "+calcWorkItemHandler.getWorkItem());
        kieSession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), res);

        kieSession.getWorkItemManager().completeWorkItem(calcWorkItemHandler.getWorkItem().getId(),null);

        //验证usertask是否执行完成
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }





    /**
     * 使用taskService测试流程中的usertask执行
     */
    @Test
    public void testUserTaskCompleteByTaskService() {

        /**
         * 解析bpmn2文件，获取引擎
         * 运行时管理器创建引擎
         * 运行时引擎创建session
         * 运行时引擎创建taskservice
         * 启动流程
         */
        createRuntimeManager("dataTransfer.bpmn2");
        RuntimeEngine engine = getRuntimeEngine(EmptyContext.get());
        KieSession ksession = engine.getKieSession();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance=ksession.startProcess("dataTransfer");

        /**
         * 设置super(true,true);否则报错
         */
        TaskService taskService = engine.getTaskService();

        /**
         * 获取任务列表
         * 循环遍历根据名字来获取指定任务
         * 给任务分配执行者，
         * 准备任务的传入参数
         * 进行任务的执行
         */

        //TODO 解决taskService找不到usertask的问题
        //目前这里会报错数组下标越界
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");

        TaskSummary taskSummary = tasks.get(0);
        taskService.start(taskSummary.getId(), "john");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("pass", "hello,world!");
        taskService.complete(taskSummary.getId(), "john", results);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    /**
     * 创建kiebase
     * @param process
     * @return
     * @throws Exception
     */
    protected KieBase createKnowledgeBase(String process) throws Exception {

        Resource classpathResource = ResourceFactory.newClassPathResource(process);
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(classpathResource);
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        return kContainer.getKieBase();

    }

    /**
     * 创建kieSession
     * @param kbase
     * @param conf
     * @param env
     * @return
     * @throws Exception
     */
    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase, KieSessionConfiguration conf, Environment env) throws Exception {
        if (conf == null) {
            conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }
        if (env == null) {
            env = EnvironmentFactory.newEnvironment();
        }
        Properties defaultProps = new Properties();
        defaultProps.setProperty("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName());
        defaultProps.setProperty("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        conf = SessionConfiguration.newInstance(defaultProps);
        conf.setOption(ForceEagerActivationOption.YES);
        StatefulKnowledgeSession result = (StatefulKnowledgeSession) kbase.newKieSession(conf, env);
        return result;
    }
}
