package com.example.qj.demo.learn;

import com.example.qj.demo.entity.MyWorkItemHandler;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IntermediateMessageOperation {

    /**
     * 测试消息中间事件的抛，捕获和边界
     * 抛出消息事件，需要设置Send Task任务句柄。由句柄自动执行
     * 捕获消息事件，需要手动设置消息的触发，代码中发送消息，由流程来捕获消息
     *
     * 边界事件仅能绑定在状态节点，比如userTask和子流程。想要执行边界事件，则主流程不能立刻执行完，然后在绑定节点处发送对应的消息触发边界事件
     * @throws Exception
     */
    @Test
    public void intermediateMessageTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateMessageOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new WorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                String message = (String) workItem.getParameter("Message");
                System.out.println("message="+message);
                /**
                 * 可以执行发送消息的操作
                 */
                manager.completeWorkItem(workItem.getId(), null);
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

            }
        });

        MyWorkItemHandler workItemHandler=new MyWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "goodmornig,everyone");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateMessage",params);

        //触发边界消息事件
        ksession.signalEvent("Message-helloMessage", "hello,world", processInstance.getId());

        //继续执行主流程
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(),null);
        //触发主流程中的消息捕获事件
        ksession.signalEvent("Message-helloMessage", "你好", processInstance.getId());

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
