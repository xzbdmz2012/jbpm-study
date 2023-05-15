package com.example.qj.demo.learn;

import com.example.qj.demo.handler.MyWorkItemHandler;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;

public class AdHocSubProcessOperation {

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
     * 测试adhocsubprocess,特定的子流程。执行者可以安排其中的任务的任意执行顺序。而且其中任务没有开始和结束节点连接
     * 可以设置并行还是串行。必须要给流程设置完成条件,否则主流程无法完成
     * @throws Exception
     */
    @Test
    public void testAdHocSubProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("rule/adHocSubProcessOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);


        MyWorkItemHandler workItemHandler = new MyWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("adHocSubProcess");
        ksession.fireAllRules();

        //可以通过发送信号，触发adhoc中的任务，索引是任务名。
        ksession.signalEvent("Hello1", null, processInstance.getId());
        ksession.signalEvent("Hello2", null, processInstance.getId());

        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
