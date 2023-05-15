package com.example.qj.demo.learn;

import com.example.qj.demo.listener.MyProcessEventListener;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
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
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BusinessRuleTaskOperation {

    /**
     * 测试业务规则任务，后续还需要学习Drools rule语法，后缀drl的文件
     * 具体的代码逻辑还需要梳理...
     * 测试业务规则任务的执行顺序
     */
    @Test
    public void businessRuleTaskTest() throws Exception {
        //这里需要同时解析bpmn2文件和drl文件
        KieBase kbase = createKnowledgeBase("businessRuleTaskOperation.bpmn2", "businessRuleTaskOperation.drl");
        KieSession ksession = createKnowledgeSession(kbase,null,null);
//        ksession.addEventListener(new MyProcessEventListener());
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ksession.startProcess("businessRuleTask");
    }

    /**
     * 创建kiebase,解析多个文件：流程文件和规则文件
     * @param process
     * @return
     * @throws Exception
     */
    protected KieBase createKnowledgeBase(String... process) throws Exception {

        List<Resource> resource=new ArrayList<>();
        for(String p:process) {
            Resource classpathResource = ResourceFactory.newClassPathResource(p);
            resource.add(classpathResource);

        }
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Resource r:resource) {
            kfs.write(r);
        }
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
