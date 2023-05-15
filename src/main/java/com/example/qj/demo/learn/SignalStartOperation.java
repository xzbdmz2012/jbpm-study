package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.KieSession;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;

public class SignalStartOperation {

    /**
     * 测试信号启动事件，和消息启动事件类似，不过信号的类型必须和<signalEventDefinition signalRef="" />一致
     * @throws Exception
     */
    @Test
    public void signalStartTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("signalStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        //增加事件监听器，监听开始事件的执行
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener(){
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ksession.signalEvent("MyStartSignal", null);
        Assertions.assertThat(list).hasSize(1);

    }

}
