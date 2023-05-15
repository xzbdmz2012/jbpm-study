package com.example.qj.demo.learn;

import com.example.qj.demo.entity.MyWorkItemHandler;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static com.example.qj.demo.learn.BoundaryErrorOperation.createKnowledgeBase;
import static org.junit.Assert.assertEquals;

public class BoundaryEscalationOperation {

    /**
     * 增量边界事件，和子流程进行绑定，如果是子流程中结束节点抛出增量，不会影响后续节点执行，但是是中间增量事件抛出，则主流程后续节点不执行
     * 这里不需要进行手动抛出，是自动执行的操作
     * @throws Exception
     */
    @Test
    public void boundaryEscalationTest() throws Exception {

        KieBase kbase = createKnowledgeBase("boundaryEscalationOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("boundaryEscalation",null );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
