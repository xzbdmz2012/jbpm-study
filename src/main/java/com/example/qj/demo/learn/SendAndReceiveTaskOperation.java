package com.example.qj.demo.learn;

import com.example.qj.demo.handler.MyReceiveTaskHandler;
import com.example.qj.demo.handler.MySendTaskHandler;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SendAndReceiveTaskOperation {

    /**
     * 测试发送任务和接收任务的使用
     */
    @Test
    public void sendAndReceiveTaskTest() throws Exception {

        KieBase kbase = createKnowledgeBase("sendAndReceiveTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase,null,null);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new MySendTaskHandler());

        MyReceiveTaskHandler receiveTaskHandler = new MyReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("res", "john");
        WorkflowProcessInstance processInstance= (WorkflowProcessInstance) ksession.startProcess("sendAndReceiveTask", params);

        receiveTaskHandler.setKnowledgeRuntime(ksession);
        receiveTaskHandler.messageReceived("_3_Message", "Hello john!");

        String sNew = (String) processInstance.getVariable("res");
        System.out.println("res final value="+sNew);
    }

    /**
     * 创建kiebase
     * @param process
     * @return
     * @throws Exception
     */
    protected KieBase createKnowledgeBase(String process) throws Exception {

        Resource classpathResource = ResourceFactory.newClassPathResource(process);
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(classpathResource);
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        return kContainer.getKieBase();

    }

    /**
     * 创建kieSession
     * @param kbase
     * @param conf
     * @param env
     * @return
     * @throws Exception
     */
    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase, KieSessionConfiguration conf, Environment env) throws Exception {
        if (conf == null) {
            conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }
        if (env == null) {
            env = EnvironmentFactory.newEnvironment();
        }
        Properties defaultProps = new Properties();
        defaultProps.setProperty("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName());
        defaultProps.setProperty("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        conf = SessionConfiguration.newInstance(defaultProps);
        conf.setOption(ForceEagerActivationOption.YES);
        StatefulKnowledgeSession result = (StatefulKnowledgeSession) kbase.newKieSession(conf, env);
        return result;
    }


}
