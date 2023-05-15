package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SignalEndOperation {

    /**
     * 测试信号结束事件，同样的是在结束节点发送信号，但是不知道信号之后去哪儿了
     * 信号结束事件不会触发Send Task任务句柄
     * @throws Exception
     */
    @Test
    public void messageEndTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("signalEndOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("signalEnd", params );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

    }

}
