package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class MessageStartOperation {

    /**
     * 测试消息启动事件，不同的启动方式
     * 信号的类型格式为Message-标签中的名字，标签如👉：<messageEventDefinition messageRef="GoodMessage"/>
     * @throws Exception
     */
    @Test
    public void messageStartTest() throws Exception {

        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("messageStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.signalEvent("Message-GoodMessage", "hello,message Start Event");
    }

}
