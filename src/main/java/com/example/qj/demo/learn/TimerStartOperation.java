package com.example.qj.demo.learn;

import com.example.qj.demo.listener.DefaultCountDownProcessEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class TimerStartOperation{

    /**
     * 测试时间开始事件，可以增加事件监听器
     * 定时事件可以只处理一次，也可以反复处理，关键是时间表达式的值设置
     * 不能直接模拟流程启动。测试的时候设置超时时间.定时任务启动
     * @throws Exception
     */
    @Test(timeout=10000)
    public void timerStartTest() throws Exception {
        DefaultCountDownProcessEventListener countDownListener = new DefaultCountDownProcessEventListener(9);
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("timerStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase,null,null);
        ksession.addEventListener(countDownListener);
        countDownListener.waitTillCompleted();
        System.out.println("时间开始事件测试完成！");
    }
}
