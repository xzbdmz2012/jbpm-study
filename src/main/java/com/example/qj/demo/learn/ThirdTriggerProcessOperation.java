package com.example.qj.demo.learn;

import com.example.qj.demo.handler.MyHumanTaskHandler;
import com.example.qj.demo.listener.MyEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.HashMap;
import java.util.Map;

public class ThirdTriggerProcessOperation {

    /**
     * 验证第三种方式进行流程间触发：
     * 手动启动流程，然后在第一流程任务的handler中，调用第二流程的启动方法，前提是session要传入第一流程的handler中
     *
     * 结果：success，一个流程至少要有一个开始节点
     */
    @Test
    public void thirdTriggerProcessOperation() throws Exception {
        KieBase kieBase= KieBaseAndKieSessionFactory.createKnowledgeBase("thirdStartProcess.bpmn2","thirdTriggerProcess.bpmn2");
        KieSession kieSession=KieBaseAndKieSessionFactory.createKnowledgeSession(kieBase);

        MyHumanTaskHandler humanTaskHandler=new MyHumanTaskHandler(kieSession);
        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);

        Map<String,Object> map=new HashMap<>();
        map.put("x","hello");
        kieSession.startProcess("thirdStartProcess",map);

    }

    /**
     * 验证第四种方式进行流程间触发：
     * 手动启动流程，然后在第一流程任务的listener中，调用第二流程的启动方法，前提是session要传入第一流程的listener中
     * TODO 未完待续...
     * 结果：
     */
    @Test
    public void fourthTriggerProcessOperation() throws Exception {
        KieBase kieBase= KieBaseAndKieSessionFactory.createKnowledgeBase("fourthStartProcess.bpmn2","fourthTriggerProcess.bpmn2");
        KieSession kieSession=KieBaseAndKieSessionFactory.createKnowledgeSession(kieBase);

        MyEventListener listener=new MyEventListener();
        kieSession.addEventListener(listener);

        Map<String,Object> map=new HashMap<>();
        map.put("x","hello");
        kieSession.startProcess("fourthStartProcess",map);

    }

}
