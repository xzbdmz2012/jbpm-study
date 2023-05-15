package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;
import static org.junit.Assert.assertEquals;


public class ParallelStartOperation {

    /**
     * 注意：在执行结果中，暂时没发现它和不并行执行有什么区别？？？
     *
     * 测试并行开始事件，它是一个属性，不是一个组件。
     * serviceTask组件如果不通过反射调用对应的service方法，那么该方法可以不存在。但是bpmn文件中对应的<interface>还是要写
     * 可以在工作项句柄中设置自动执行
     * @throws Exception
     */
    @Test
    public void parallelStartTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("parallelStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        //注册工作项句柄的时候，key的值是固定的，Service Task或者Human Task，其他值会报错找不到句柄
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new WorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                System.out.println("Executing work item " + workItem);
                manager.completeWorkItem(workItem.getId(), null);
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                System.out.println("Aborting work item " + workItem);
                manager.abortWorkItem(workItem.getId());
            }
        });
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("parallelStart");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

    }

}
