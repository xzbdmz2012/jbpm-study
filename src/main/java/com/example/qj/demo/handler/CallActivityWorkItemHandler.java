package com.example.qj.demo.handler;

import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public class CallActivityWorkItemHandler implements WorkItemHandler {

    private WorkItem workItem;
    private WorkItemManager manager;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        this.workItem=workItem==null?null:workItem;
        this.manager=manager==null?null:manager;
        System.out.println("CallActivityWorkItemHandler is executing...");
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        this.workItem=workItem==null?null:workItem;
        this.manager=manager==null?null:manager;
        System.out.println("CallActivityWorkItemHandler is aborted...");

    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public WorkItemManager getManager() {
        return manager;
    }

    public void setManager(WorkItemManager manager) {
        this.manager = manager;
    }
}
