package com.example.qj.demo.learn;

import com.example.qj.demo.entity.MyWorkItemHandler;
import com.example.qj.demo.listener.DefaultCountDownProcessEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import static com.example.qj.demo.learn.BoundaryErrorOperation.createKnowledgeBase;
import static org.junit.Assert.assertEquals;

public class BoundaryTimerOperation {

    /**
     * 测试时间边界事件。当子流程的任务在执行时间内都没有执行，那么会触发时间边界事件，类似超时处理的操作，并且只触发一遍
     * 如果后续任务正确执行了，主流程后面依然可以执行。
     *
     * <task>标签需要指定tns:taskName="MyTask"任务名属性
     * 具体的任务标签只需注册句柄时指定任务类型即可
     *
     * @throws Exception
     */
    @Test
    public void boundaryTimerTest() throws Exception {

        DefaultCountDownProcessEventListener countDownListener = new DefaultCountDownProcessEventListener(9);

        KieBase kbase = createKnowledgeBase("boundaryTimerOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);

        MyWorkItemHandler workItemHandler=new MyWorkItemHandler();

        //注册任务句柄，可以使用指定的任务名或者任务的类型。
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",workItemHandler);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("boundaryTimer");

        Thread.sleep(1000);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(),null);

        long startTime = System.currentTimeMillis();
        countDownListener.waitTillCompleted();
        long endTime = System.currentTimeMillis();
        System.out.println("流程执行的事件：" + (endTime - startTime) + "毫秒");



        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }



}
