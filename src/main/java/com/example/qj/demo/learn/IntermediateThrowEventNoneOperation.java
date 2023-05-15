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

public class IntermediateThrowEventNoneOperation {

    /**
     * 中间事件只要有3种组件，抛出事件<intermediateThrowevent>，捕获事件<intermediateCatchevent>，边界事件<BoundaryEvent>
     * 空中间事件知识一个普通的节点,只能抛出
     * @throws Exception
     */
    @Test
    public void intermediateThrowEventNoneTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateThrowEventNone.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateThrowEventNone");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
