package com.example.qj.demo.handler;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DemoServiceTaskHandler implements WorkItemHandler {

    /**
     * 使用反射调用serviceTask方法
     * @param workItem
     * @param manager
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        System.out.println("工作项的所有参数！！！=="+workItem.getParameters());

        //每一个变量的名字都是固定的。
        //获取interface标签的name属性值
        String service = (String) workItem.getParameter("Interface");
        //获取interfaceImplementationRef,暂时为null
        String interfaceImplementationRef = (String) workItem.getParameter("interfaceImplementationRef");
        //获取operation标签的name属性值
        String operation = (String) workItem.getParameter("Operation");
        //获取传入的参数类型
        String parameterType = (String) workItem.getParameter("ParameterType");
        //获取方法参数的值，目前是单个参数
        Object parameter = workItem.getParameter("Parameter");

        String[] services = {service, interfaceImplementationRef};
        Class<?> c = null;

        for(String serv : services) {
            try {
                c = Class.forName(serv);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            Object instance = c.newInstance();
            Class<?>[] classes = null;
            Object[] params = null;
            if (parameterType != null) {
                classes = new Class<?>[]{Class.forName(parameterType)};
                params = new Object[]{parameter};
            }
            Method method = c.getMethod(operation, classes);
            Object result = method.invoke(instance, params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", result);
            manager.completeWorkItem(workItem.getId(), results);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }

}
