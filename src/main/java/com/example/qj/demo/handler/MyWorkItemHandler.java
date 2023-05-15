package com.example.qj.demo.handler;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.springframework.stereotype.Component;

@Component
public class MyWorkItemHandler implements WorkItemHandler {

    private WorkItem workItem;
    private WorkItemManager workItemManager;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        this.workItem=workItem!=null?workItem:null;
        this.workItemManager=workItemManager!=null?workItemManager:null;
        System.out.println("workitem is executing...");
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        this.workItem=workItem!=null?workItem:null;
        this.workItemManager=workItemManager!=null?workItemManager:null;
        System.out.println("workitem is aborted!");
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public WorkItemManager getWorkItemManager() {
        return workItemManager;
    }

    public void setWorkItemManager(WorkItemManager workItemManager) {
        this.workItemManager = workItemManager;
    }

    @Override
    public String toString() {
        return "MyWorkItemHandler{" +
                "workItem=" + workItem +
                ", workItemManager=" + workItemManager +
                '}';
    }
}
