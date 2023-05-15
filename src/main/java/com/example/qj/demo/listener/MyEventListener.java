package com.example.qj.demo.listener;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.runtime.KieSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class MyEventListener extends DefaultProcessEventListener {

    public MyEventListener() {
    }


    /**
     * 使用监听器触发新流程
     * @param event
     */
    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        if( event.getNodeInstance().getNodeName().equals("fourthTrigger") ){

            KieSession kieSession= (KieSession) event.getKieRuntime();
            String res= (String) event.getNodeInstance().getVariable("x");

            Map<String,Object> map=new HashMap<>();
            map.put("y",res);
            kieSession.startProcess("fourthTriggerProcess",map);
        }

    }

}
