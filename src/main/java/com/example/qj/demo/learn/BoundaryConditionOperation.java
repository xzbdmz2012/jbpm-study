package com.example.qj.demo.learn;

import com.example.qj.demo.entity.MyWorkItemHandler;
import com.example.qj.demo.entity.Person;
import com.example.qj.demo.listener.DefaultCountDownProcessEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.io.ResourceFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BoundaryConditionOperation {

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
     * 测试条件边界事件，在用户任务绑定条件边界事件，当主流程执行到用户任务时，人工触发条件，则调用任务句柄的executeWorkItem()方法。但是任务没有执行完
     * 后续如果调用compete方法，任务才算执行完。条件边界不影响主流程的执行，触边界事件的结束节点是全局终止。
     * 如果主流程结束，绑定的条件事件自动结束。
     *
     * completeWorkItem方法内部包含调用任务句柄的executeWorkItem方法，还有完成任务方法。
     * 当条件边界触发任务的executeWorkItem()方法，completeWorkItem方法不会再执行executeWorkItem()方法。
     * 如果没有，则两个方法前后顺序调用
     *
     * @throws Exception
     */
    @Test
    public void boundaryConditionTest() throws Exception {

        KieBase kbase = createKnowledgeBase("boundaryConditionOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        MyWorkItemHandler workItemHandler=new MyWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",workItemHandler);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("boundaryCondition",null );

        //触发条件边界事件
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);

        //触发主流程执行
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(),null );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }



}
