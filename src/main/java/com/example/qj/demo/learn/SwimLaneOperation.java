package com.example.qj.demo.learn;

import com.example.qj.demo.handler.MyWorkItemHandler;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SwimLaneOperation {

    /**
     * 测试泳道的任务分配作用。----用于分配任务，给指定泳道的人分配任务，类似于全局用户组。
     * 使用场景：流程定义中的多个任务需要被分配或候选给同一个群用户。那么我们可以统一将这个“同一群用户”定义为“一个泳道”
     *
     * @throws Exception
     */
    @Test
    public void testAdHocSubProcess() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("rule/swimLaneOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        MyWorkItemHandler workItemHandler = new MyWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("swimLane");

        System.out.println("当前用户的执行人"+workItemHandler.getWorkItem().getParameters());

        //换个人执行任务
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ActorId", "mary");
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), results);

        System.out.println("最新的用户的执行人"+workItemHandler.getWorkItem().getParameters());
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), results);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
