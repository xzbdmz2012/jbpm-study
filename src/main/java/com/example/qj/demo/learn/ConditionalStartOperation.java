package com.example.qj.demo.learn;

import com.example.qj.demo.entity.Person;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

public class ConditionalStartOperation {

    /**
     * 测试条件开始事件，通过drools规则的事件，传入指定事件，触发drools规则，进而启动流程
     * jbpm引擎嵌套了drools的解析器，在bpmn文件中使用drools语法，能够在流程中执行
     * 在bpmn文件中可以引入其他文件。格式如下：使用<tns:import>组件
     * <extensionElements>
     *      <tns:import name="com.example.qj.demo.entity.Person" />
     * </extensionElements>
     */
    @Test
    public void condtionalStartTest() throws Exception {
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("conditionalStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        Person person = new Person("jack");
        ksession.insert(person);
        ksession.fireAllRules();
        System.out.println("jack规则触发...");

        person=new Person("john");
//        person.setName("john");

        /**
         * 注意事项：如果在条件开始事件中，最开始会话中插入了一个条件触发需要的指定属性值的对象，那么该流程会启动
         * 如果一开始插入的对象的属性值不对，则不会触发。之后直接修改该对象的属性值，也不会继续触发。只有重新new一个正确值的对象，它才会触发
         * 猜测jbpm引擎中嵌套的drools引擎可能有缓存机制，同一个对象到来时，他会比较对象地址，不管该对象的值后来是否正确，一致则使用缓存对象。
         * 关键！！！：即使重写对象的equals方法也没用.
         */
        ksession.insert(person);
        ksession.fireAllRules();
        System.out.println("john规则触发...");
    }

}
