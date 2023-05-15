package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import static org.junit.Assert.assertEquals;

public class EscalationStartOperation {

    /**
     * 测试增量开始事件，暂时发现可以用来代替sequenceFlow触发子流程
     *
     * 一个bpmn文件中，有主流程和子流程，可以使用<sequenceFlow>来连接执行。如果没有，则会报错子流程必须要有触发器
     * 子流程的触发器可以使用增量组件<bpmn2:escalationEventDefinition escalationRef="escId2" />
     * 如果主流程没有增量组件，则执行该流程文件时，只会执行主流程
     * 当主流程和子流程都有增量组件时，标签<bpmn2:escalation id="escId" escalationCode="escCode" />的取值任意皆可，
     * 并且主流程的结束事件的增量组件的名字和子流程的开始事件的增量组件不同名都可以！！！
     *
     * <bpmn2:escalation id="escId" escalationCode="escCode" />
     * @throws Exception
     */
    @Test
    public void escalationStartTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("escalationStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("escalationStart");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

    }

}
