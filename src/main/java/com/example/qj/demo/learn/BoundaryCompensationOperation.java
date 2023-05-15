package com.example.qj.demo.learn;

import com.example.qj.demo.entity.MyWorkItemHandler;
import com.example.qj.demo.entity.Person;
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

public class BoundaryCompensationOperation {

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
     * 补偿事件使用association进行节点连接，而不是使用顺序流
     * 边界补偿事件的分支，不能有结束节点
     *
     * 有点类似于在两个节点中间嵌入了一个节点的行为
     * @throws Exception
     */
    @Test
    public void boundaryCompensationTest() throws Exception {

        KieBase kbase = createKnowledgeBase("boundaryCompensationOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("boundaryCompensation",null );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }



}
