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
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayOperation{

    /**
     * 测试排他网关
     * 每条分支上有过滤条件，最少一条支路是通的
     * 多个条件执行为true，遇到第一个路径就会朝该路径执行
     * xml中的小于符号使用<![CDATA[ ]]>
     * bpmn2文件报错，可能是definitions标签中的这条语句未添加：targetNamespace="http://www.jboss.org/drools"
     */
    @Test
    public void gatewayXORTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("gatewayXOR.bpmn2");
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);
        Map<String,Object> map=new HashMap<>();

        Random random=new Random();
        int index=100;
        while(index>0) {

            int x=random.nextInt(100)+1;
            int y=random.nextInt(100)+1;
            while(x<=50 && y>50) {
                System.out.println("当前的x,y值不符合条件。x="+x+" y="+y);
                x=random.nextInt(100)+1;
                y=random.nextInt(100)+1;
            }

            map.put("x", x);
            map.put("y", y);
            ProcessInstance processInstance=kieSession.startProcess("gatewayXOR", map);
            System.out.println("x="+x+" y="+y+" "+processInstance.toString());

            kieSession=kieBase.newKieSession();
            index--;
        }
    }

    /**
     * 测试并行网关,多个分支的完成顺序是随机的
     * 分支全部自动触发
     */
    @Test
    public void gatewayANDTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("gatewayAND.bpmn2");
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);
        Map<String,Object> map=new HashMap<>();

        Random random=new Random();
        int index=100;
        while(index>0) {
            map.put("x", random.nextInt(100)+1);
            map.put("y", random.nextInt(100)+1);
            ProcessInstance processInstance=kieSession.startProcess("gatewayAND", map);
            System.out.println(processInstance.toString());

            kieSession=kieBase.newKieSession();
            index--;
        }
    }

    /**
     * 测试包含网关
     * 每个过滤条件都会被执行，结果为true，则会走这条分支。如果没条件设置，则肯定执行
     * 必须至少一条分支满足
     */
    @Test
    public void gatewayORTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("gatewayOR.bpmn2");
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);
        Map<String,Object> map=new HashMap<>();

        Random random=new Random();
        int index=100;
        while(index>0) {
            int x=random.nextInt(100)+1;
            while(x<=30){
                System.out.println("x的取值不满足条件。x="+x);
                x=random.nextInt(100)+1;
            }
            map.put("x", x);
            ProcessInstance processInstance=kieSession.startProcess("gatewayOR", map);
            System.out.println("x="+x+" "+processInstance.toString());

            kieSession=kieBase.newKieSession();
            index--;
        }
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
