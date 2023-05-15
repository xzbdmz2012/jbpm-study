package com.example.qj.demo.learn;

import com.example.qj.demo.handler.CallActivityWorkItemHandler;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
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
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CallActivityOperation {

    /**
     * 测试callActivity的使用，包括流程文件的调用，数据在子流程中的传递，自流程中的handler操作，同时调用多个流程文件
     * 多流程返回到主流程的的数据合并问题
     * 同时调用两个名字相同的流程，版本号高的才会执行，如果版本号一致或者都没有,猜测简单验证了一下，只调用无用户任务的那个,
     * 尝试着修改写入知识库的顺序，但没有效果
     *
     * 如两个被调用子流程ID一致，并且主流程通过ID查找，则报错找不到kmodule
     */
    @Test
    public void callActivityTest() throws Exception {
        KieBase kieBase=createKnowledgeBase("callActivityOperation.bpmn2",
                "subCallActivityOne.bpmn2","subCallActivityTwo.bpmn2"
                );
        KieSession kieSession=createKnowledgeSession(kieBase,null,null);

        //注册usertask的工作项句柄
        CallActivityWorkItemHandler workItemHandler = new CallActivityWorkItemHandler();
        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String,Object> map=new HashMap<>();
        map.put("x","how are you?");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)kieSession.startProcess("callActivityParent",map);

        WorkItem workItem = workItemHandler.getWorkItem();
        System.out.println(workItem.getParameters());
        map.put("name","jxiong");
        kieSession.getWorkItemManager().completeWorkItem(workItem.getId(), map);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());


        //将kieSession资源释放，方便垃圾回收
        kieSession.dispose();

    }

    /**
     * 创建kiebase,同时导入多个流程文件
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
