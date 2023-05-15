package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class MessageEndOperation {

    /**
     * 测试消息结束事件，必须需要一个send task任务句柄来进行消息处理
     * 发消息的任务句柄，没有调用completeWorkItem方法，不会影响流程的执行状态
     *
     * 暂时不知道消息发送到何处去了？
     */
    @Test
    public void messageEndTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("messageEndOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new WorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                //这个方法的执行并不会影响流程的执行状态,消息参数的名字为Message
                System.out.println(workItem.getParameters());
                manager.completeWorkItem(workItem.getId(),null);
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

            }
        });
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("messageEnd", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

    }

}
