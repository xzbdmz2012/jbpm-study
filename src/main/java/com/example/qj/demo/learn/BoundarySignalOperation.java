package com.example.qj.demo.learn;

import com.example.qj.demo.entity.MyWorkItemHandler;
import com.example.qj.demo.entity.Person;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static com.example.qj.demo.learn.BoundaryErrorOperation.createKnowledgeBase;
import static org.junit.Assert.assertEquals;

public class BoundarySignalOperation {

    /**
     * 测试信号边界事件，边界事件可以绑定到子流程或者Usertask节点上
     * @throws Exception
     */
    @Test
    public void boundarySignalTest() throws Exception {

        KieBase kbase = createKnowledgeBase("boundarySignalOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        MyWorkItemHandler workItemHandler=new MyWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",workItemHandler);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("boundarySignal",null );

        //触发信号边界事件
        ksession.signalEvent("MyMessage", null);

        //触发主流程执行
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(),null );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
