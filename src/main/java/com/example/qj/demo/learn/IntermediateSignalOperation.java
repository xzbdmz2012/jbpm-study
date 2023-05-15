package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IntermediateSignalOperation {

    /**
     * 测试信号中间事件
     * 动态设置信号标签测试没成功......
     * @throws Exception
     */
    @Test
    public void intermediateSignalTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateSignalOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        ProcessInstance processInstance = ksession.startProcess("intermediateSignal");

        ksession.signalEvent("myVarSignal", "SomeValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

    }

}
