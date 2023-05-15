package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FirstTriggerProcessOperation {

    /**
     * 验证第一种方式进行流程间触发：
     * 手动启动流程，然后在第一流程的结束节点发送消息，第二流程的开始节点接收消息，从而启动第二流程
     *
     * 结果：信号事件能够成功执行，但是只能传递单个参数，多个参数传递只会接收最后一个参数。而且消息事件暂时不能。
     */
    @Test
    public void firstTriggerProcessOperation() throws Exception {
        KieBase kieBase= KieBaseAndKieSessionFactory.createKnowledgeBase("firstStartProcess.bpmn2","firstTriggerProcess.bpmn2");
        KieSession kieSession=KieBaseAndKieSessionFactory.createKnowledgeSession(kieBase);

        Map<String,Object> map=new HashMap<>();
        map.put("x","hello");
        ProcessInstance processInstance=kieSession.startProcess("firstStartProcess",map);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
