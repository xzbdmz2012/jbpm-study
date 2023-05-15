package com.example.qj.demo.learn;

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
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = testWorkFlow.class)
public class testWorkFlow {

    /**
     * 测试数据的映射，简单实现传入a，b参数，完成加法操作
     * bomn2文件位置在resources下
     * 程序报异常找不到kiemodule,说明bpmn2.0文件语法有误
     */
    @Test
    public void addOperationTest() throws Exception {

        //1.创建kiebase
        KieBase kbase = createKnowledgeBase("leaveprocess.bpmn");
        //2.创建kieSession
        KieSession ksession = createKnowledgeSession(kbase,null,null);
        //3.启动流程，并传入参数
        Map<String, Object> params = new HashMap<String, Object>();


        params.put("applicant", "admin");
        // 初始化一些默认值
        params.put("applicantSubmit", false);
        params.put("leaderAgree", false);
        params.put("directorAgree", false);
        params.put("hrRecord", false);
        params.put("leader","admin");
        params.put("director","admin");
        params.put("hr","admin");


            ProcessInstance processInstance = ksession.startProcess("leaveProcess", params);
            System.out.println("id="+processInstance.getId()
                               +" process="+processInstance.getProcess()
                               +" processid="+processInstance.getProcessId()
                               +" processname="+processInstance.getProcessName()
                               +" state="+processInstance.getState()
                               +" eventTypes="+processInstance.getEventTypes()
                               +" parentProcessInstanceId="+processInstance.getParentProcessInstanceId());

            ksession=kbase.newKieSession();

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
    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase,KieSessionConfiguration conf, Environment env) throws Exception {
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
