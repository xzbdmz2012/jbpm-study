package com.example.qj.demo.learn;

import com.example.qj.demo.exception.MyError;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import static org.junit.Assert.assertEquals;

public class ErrorStartOperation {

    /**
     * 测试错误开始事件，主要在子流程的触发中。
     * 使用组件<errorEventDefinition id="ErrorEventDefinition_2" errorRef="error"/>和<error id="error" errorCode="com.example.qj.demo.exception.MyError"/>
     * 如果在任务中直接抛出异常，那么主流程的后续节点不再执行，从子流程开始执行
     *
     * 和增量开始事件一致，<error>对应的组件的两个属性取值随意
     * 事件监听起中不能抛出异常
     *
     * @throws Exception
     */
    @Test
    public void errorStartTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("errorStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                //用户任务中可以抛出异常，触发指定的处理流程.
                throw new MyError();
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.abortWorkItem(workItem.getId());
            }
        });

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("errorStart");


        /**
         * 触发异常子流程,可以直接在bpmn的主流程的节点中设置触发器
         * 也可以在工作项句柄中抛出指定异常进行触发
         * 当抛出异常后，流程的状态变为ProcessInstance.STATE_ABORTED=3
         * 当直接在流程中主流程结束节点进行触发时，流程状态为ProcessInstance.STATE_COMPLETED=2
         * 当Human Task的句柄没有执行时，流程状态为ProcessInstance.STATE_ACTIVE=1
         */
        assertEquals(ProcessInstance.STATE_ABORTED, processInstance.getState());

    }

}
