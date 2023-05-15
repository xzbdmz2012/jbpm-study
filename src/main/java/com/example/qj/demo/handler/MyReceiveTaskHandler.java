package com.example.qj.demo.handler;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import java.util.HashMap;
import java.util.Map;

public class MyReceiveTaskHandler implements WorkItemHandler {

    private Map<String, Long> map = new HashMap<String, Long>();
    private ProcessRuntime ksession;

    public MyReceiveTaskHandler(KieSession ksession) {
        this.ksession = ksession;
    }

    public void setKnowledgeRuntime(KieSession ksession) {
        this.ksession = ksession;
    }

    public void messageReceived(String messageId, Object message) {
        Long workItemId = map.get(messageId);
        if (workItemId == null) {
            return;
        }
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("receiveMessage", message);
        System.out.println("parameterMap="+results);
        ksession.getWorkItemManager().completeWorkItem(workItemId, results);
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("第二流程执行到此！");
        String messageId = (String) workItem.getParameter("MessageId");
        map.put(messageId, workItem.getId());
        System.out.println("execute: workItem="+workItem.getParameters()+" MessageId="+messageId+" map="+map);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        String messageId = (String) workItem.getParameter("MessageId");
        map.remove(messageId);
        System.out.println("abort:MessageId="+messageId+" map="+map);
    }

}
