package com.example.qj.demo.learn;

import com.example.qj.demo.entity.Person;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static org.junit.Assert.assertEquals;

public class IntermediateEscalationOperation {

    /**
     * 中间增量事件，只抛出增量，流程会终止，不会正常结束
     * @throws Exception
     */
    @Test
    public void intermediateConditionTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateEscalationOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateEscalation");
        assertEquals(ProcessInstance.STATE_ABORTED, processInstance.getState());
    }

}
