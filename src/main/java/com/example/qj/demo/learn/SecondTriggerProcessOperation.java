package com.example.qj.demo.learn;

import com.example.qj.demo.handler.MyReceiveTaskHandler;
import com.example.qj.demo.handler.MySendTaskHandler;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SecondTriggerProcessOperation {

    /**
     * 验证第二种方式进行流程间触发：
     * 手动启动流程，然后在第一流程sendTask节点发送消息，然后被第二流程的已经开始的receiveTask接收到，继续执行第二流程
     *
     * 结果：success，一个流程至少要有一个开始节点
     */
    @Test
    public void secondTriggerProcessOperation() throws Exception {
        KieBase kieBase= KieBaseAndKieSessionFactory.createKnowledgeBase("secondStartProcess.bpmn2","secondTriggerProcess.bpmn2");
        KieSession kieSession=KieBaseAndKieSessionFactory.createKnowledgeSession(kieBase);

        MySendTaskHandler sendTaskHandler=new MySendTaskHandler();
        MyReceiveTaskHandler receiveTaskHandler=new MyReceiveTaskHandler(kieSession);
        kieSession.getWorkItemManager().registerWorkItemHandler("Send Task",sendTaskHandler );
        kieSession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);

        Map<String,Object> map=new HashMap<>();
        map.put("x","hello");
        kieSession.startProcess("secondStartProcess",map);

        kieSession.startProcess("secondTriggerProcess",null);
        receiveTaskHandler.messageReceived("myMessage","world");

    }

}
