package com.example.qj.demo.learn;

import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IntermediateCancelOperation {

    /**
     * 测试取消事件的执行顺序。源代码中没有找到对应的代码，只有一个bpmn文件。目前测试并没有通过。细节显示取消事件需要连接到结束节点 和 进行事件类型声明
     * 但是源码中没有对应的属性
     * TODO 找解决办法。。。
     *
     * 这个文件的流程是，初始化x=normal,启动流程，进入事务节点。打印x的值，经过排他网关，走x=normal路线，正常结束流程
     * 另外，初始化x=cancel,启动流程，进入事务节点。打印x的值，经过排他网关，走x=cancel路线，达到取消结束节点，抛出取消信息，在事务节点绑定的取消捕获事件被触发。继续打印x,
     * 然后进入事务中的补偿事件，打印x的值
     * @throws Exception
     */
    @Test
    public void intermediateCancelTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateCancelOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        Map<String,Object> map=new HashMap<>();
//        map.put("x","normal");
        map.put("x","cancel");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateCancel",map);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
