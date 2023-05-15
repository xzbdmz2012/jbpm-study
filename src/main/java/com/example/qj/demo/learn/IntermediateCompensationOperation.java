package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IntermediateCompensationOperation {

    /**
     * 测试中间补偿事件的抛出。抛出补偿事件不会影响后续节点的执行
     * 补偿事件结束后，才会执行主流程的下一个节点。抛出和捕获时，都需要指定进行处理的节点的位置，
     * @throws Exception
     */
    @Test
    public void intermediateCompensationTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateCompensationOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateCompensation",params);

        assertEquals(processInstance.getVariable("x"),"1");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
