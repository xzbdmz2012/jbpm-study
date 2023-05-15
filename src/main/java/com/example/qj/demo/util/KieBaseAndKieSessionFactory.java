package com.example.qj.demo.util;

import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KieBaseAndKieSessionFactory {

    public static KieBase createKnowledgeBase(String... process) throws Exception {
        List<Resource> list=new ArrayList<>();
        for (String p:process) {
            Resource classpathResource = ResourceFactory.newClassPathResource(p);
            list.add(classpathResource);
        }
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Resource r:list) {
            kfs.write(r);
        }

        //第二种创建打包版本的方法
//        ReleaseId releaseId = ks.newReleaseId("org.jbpm", "helloworld", "1.0");
//        kfs.generateAndWritePomXML(releaseId);

        //第一种创建打包版本的方法。默认值kr.getDefaultReleaseId()
        KieRepository kr = ks.getRepository();
        ReleaseId releaseId=kr.getDefaultReleaseId();
        KieContainer kContainer = ks.newKieContainer(releaseId);
        return kContainer.getKieBase();
    }

    /**
     * 创建kiesession，只有kiebase参数
     * @param kbase
     * @return
     * @throws Exception
     */
    public static StatefulKnowledgeSession createKnowledgeSession(KieBase kbase) throws Exception {
       return createKnowledgeSession(kbase,null,null);
    }

    /**
     * 创建kieSession,参数有知识库kiebase，会话配置kiesessionconfiguration，环境environment
     * @param kbase
     * @param conf
     * @param env
     * @return
     * @throws Exception
     */
    public static StatefulKnowledgeSession createKnowledgeSession(KieBase kbase, KieSessionConfiguration conf, Environment env) throws Exception {
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

    /**
     * 重新打包流程版本
     * @param deploymentId
     * @param bpmn
     */
    public static void reloadKieContainer(String deploymentId,String... bpmn){

        List<Resource> list=new ArrayList<>();
        for (String  p:bpmn) {
            Resource classpathResource = ResourceFactory.newClassPathResource(p);
            list.add(classpathResource);
        }

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Resource r:list) {
            kfs.write(r);
        }


        String[] gav = deploymentId.split(":");
        ReleaseId releaseId = ks.newReleaseId(gav[0], gav[1], gav[2]);
        kfs.generateAndWritePomXML(releaseId);

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();

        KieContainer kContainer = ks.newKieContainer(releaseId);
    }
}
