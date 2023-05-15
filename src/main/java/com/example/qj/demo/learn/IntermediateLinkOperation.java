package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static org.junit.Assert.assertEquals;

public class IntermediateLinkOperation {

    /**
     * 测试连接中间事件。连接抛出事件通过节点定义，触发下一个连接捕获事件，通过名字来进行匹配，就像双节棍一样。替代了顺序流
     * @throws Exception
     */
    @Test
    public void intermediateLinkTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateLinkOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateLink");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
