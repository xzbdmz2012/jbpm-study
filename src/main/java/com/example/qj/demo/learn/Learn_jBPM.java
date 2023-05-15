package com.example.qj.demo.learn;

import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class Learn_jBPM {

    /**
     * create process by bpmn file ,file locate on resources
     */
    @Test
    public void createProccessByBpmn(){
        KieHelper kieHelper = new KieHelper();
        KieBase kieBase = kieHelper.addResource(ResourceFactory.newClassPathResource("rule/hello.bpmn")).build();
        KieSession kieSession=kieBase.newKieSession();
        kieSession.startProcess("hello");
    }

    /**
     * create manual process and execute process
     */
    @Test
    public void createProcessByManual(){
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.HelloWorld");
        factory.name("HelloWorldProcess").version("1.0").packageName("org.jbpm")
                .startNode(1).name("Start").done()
                .actionNode(2).name("Action").action("java", "System.out.println(\"Hello World\");").done()
                .endNode(3).name("End").done()
                .connection(1, 2)
                .connection(2, 3);
        RuleFlowProcess process = factory.validate().getProcess();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        Resource resource = ks.getResources().newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        resource.setSourcePath("helloworld.bpmn2");
        kfs.write(resource);
        ReleaseId releaseId = ks.newReleaseId("org.jbpm", "helloworld", "1.0");
        kfs.generateAndWritePomXML(releaseId);
        ks.newKieBuilder(kfs).buildAll();
        ks.newKieContainer(releaseId).newKieSession().startProcess("org.jbpm.HelloWorld");
    }

}
