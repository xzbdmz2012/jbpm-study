package com.example.qj.demo.entity;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class MyWorkItemHandler implements WorkItemHandler {

    private WorkItem workItem;
    private WorkItemManager workItemManager;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        this.workItem=workItem;
        this.workItemManager=manager;
        System.out.println("user task is executing");
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        this.workItem=workItem;
        this.workItemManager=manager;
        System.out.println("user task is aborting");
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
