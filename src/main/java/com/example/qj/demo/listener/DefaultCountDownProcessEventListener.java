package com.example.qj.demo.listener;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class DefaultCountDownProcessEventListener extends DefaultProcessEventListener {

    protected CountDownLatch latch;

    public DefaultCountDownProcessEventListener() {
    }

    public DefaultCountDownProcessEventListener(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("Interrputed thread while waiting for all triggers");
        }
    }

    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrputed thread while waiting for all triggers");
        }
    }

    /**
     * 这是当流程离开每个节点后，都会执行的操作。
     * 触发的操作类似新开线程执行，不一定比流程主操作速度快
     * @param event
     */
    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        System.out.println("当前节点名字"+event.getNodeInstance().getNodeName());
        System.out.println("EnventDate="+event.getEventDate()+
                " KieRunTime="+event.getKieRuntime()+
                " NodeInstance="+event.getNodeInstance()+
                " ProcessInstance="+event.getProcessInstance());
        latch.countDown();
    }
}
