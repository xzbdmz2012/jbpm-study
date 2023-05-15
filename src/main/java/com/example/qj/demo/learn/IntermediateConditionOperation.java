package com.example.qj.demo.learn;

import com.example.qj.demo.entity.Person;
import com.example.qj.demo.listener.DefaultCountDownProcessEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static org.junit.Assert.assertEquals;

public class IntermediateConditionOperation {

    /**
     * 测试条件中间事件，同样是只能捕获，有外部发送触发条件，比如名字为jack的person对象等
     * 条件触发可以在开始，也可以在中间的任意节点。一般需要导入特定的文件
     *
     * 导入文件的方法是： <extensionElements>
     *                      <tns:import name="com.example.qj.demo.entity.Person" />
     *                  </extensionElements>
     * @throws Exception
     */
    @Test
    public void intermediateConditionTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("intermediateConditionOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("intermediateCondition");

        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);  //将对应的条件插入即可自动触发
//        ksession.fireAllRules();  //也可以通过fireAllRules进行触发

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
