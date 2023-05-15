package com.example.qj.demo.learn;

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
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;

public class BoundaryErrorOperation {

    /**
     * 测试错误边界事件，子流程结束节点抛出，自动触发。不会中断主流程的执行
     * @throws Exception
     */
    @Test
    public void boundaryErrorTest() throws Exception {

        KieBase kbase = createKnowledgeBase("boundaryErrorOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("boundaryError",null );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    /**
     * 创建kiebase
     * @param process
     * @return
     * @throws Exception
     */
    public static KieBase createKnowledgeBase(String process) throws Exception {

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
}
