package com.example.qj.demo.learn;

import com.example.qj.demo.listener.DefaultCountDownProcessEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IntermediateTimerOperation {

    /**
     * 测试时间中间事件，效果类似于在流程中进行了睡眠，过了时间后再继续执行
     * 它只能够捕获。
     * 对应时间事件的测试，需要使用线程计数器。
     *
     * 对监听事件进行方法重写，节点之间的执行顺序暂时没看出有什么规律，因此不能够进行对某个变量的顺序操作
     * 猜测和栈有关，以时间中间事件为分割，前面的栈形式执行监听操作，后面的也是栈形式执行监听操作
     * @throws Exception
     */
    @Test
    public void intermediateTimerTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateTimerOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        DefaultCountDownProcessEventListener countDownListener = new DefaultCountDownProcessEventListener(5);
        ksession.addEventListener(countDownListener);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateTimer");

        long startTime = System.currentTimeMillis();
        countDownListener.waitTillCompleted();
        long endTime = System.currentTimeMillis();
        System.out.println("流程执行的事件：" + (endTime - startTime) + "毫秒");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

    }

}
