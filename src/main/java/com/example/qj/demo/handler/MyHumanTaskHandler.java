package com.example.qj.demo.handler;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

import java.util.*;

public class MyHumanTaskHandler implements WorkItemHandler {

    private ProcessRuntime ksession;

    public MyHumanTaskHandler(KieSession ksession) {
        this.ksession = ksession;
    }

    public void setKnowledgeRuntime(KieSession ksession) {
        this.ksession = ksession;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("第一流程执行到此！");

        String res= (String) workItem.getParameter("value");

        //开始第二流程
        Map<String,Object> map=new HashMap<>();
        map.put("y",res);
        ksession.startProcess("thirdTriggerProcess",map);

        //完成第一流程的任务
        manager.completeWorkItem(workItem.getId(),null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing
    }

}
