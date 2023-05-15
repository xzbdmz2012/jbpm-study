package com.example.qj.demo.learn;

import com.example.qj.demo.exception.MyError;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CompensationStartOperation {

    /**
     * 测试补偿开始事件，一般是子流程中启动
     * 这个bpmn图的内容是，主流程中有连接的子流程，子流程还有第2层子流程。但是该子流程没有顺序流进行连接
     * 当主流程结束节点执行时，触发补偿机制，第二层子流程的开始事件被触发。设置上下文变量。
     *
     * 补偿组件需要指定活动的ID，从什么地方的开始补偿，也是触发子流程机制的一种。这个不需要额外的标签
     *<bpmn2:compensateEventDefinition id="CompensateEventDefinition_1" activityRef="_2" waitForCompletion="true"/>
     *
     * @throws Exception
     */
    @Test
    public void compensationStartTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("compensationStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

                //当人工任务没有完成时，流程会挂起
                manager.completeWorkItem(workItem.getId(),null);
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.abortWorkItem(workItem.getId());
            }
        });

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("compensationStart",params);

        //获取流程变量
        String x= (String) processInstance.getVariable("x");
        assertEquals("1",x);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
