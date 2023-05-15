package com.example.qj.demo.learn;

import com.example.qj.demo.entity.UserTaskWorkItemHandler;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
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
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserTaskOperation.class)

public class UserTaskOperation {

    /**
     * 简单测试用户任务
     */
    @Test
    public void userTaskTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("userTask.bpmn2");
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);

        //注册usertask的工作项句柄
        UserTaskWorkItemHandler workItemHandler = new UserTaskWorkItemHandler();
        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kieSession.startProcess("userTask");

        WorkItem workItem = workItemHandler.getWorkItem();
        System.out.println(workItem.getParameters());
        kieSession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        //将kieSession资源释放，方便垃圾回收
        kieSession.dispose();
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
