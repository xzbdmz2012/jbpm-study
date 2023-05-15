package com.example.qj.demo.handler;

import com.example.qj.demo.service.CalcTwoNumberService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class CalcWorkItemHandler implements WorkItemHandler {

    private WorkItem workItem;
    private WorkItemManager workItemManager;
    @Autowired
    private CalcTwoNumberService calcTwoNumberService;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        this.workItem=workItem!=null?workItem:null;
        this.workItemManager=workItemManager!=null?workItemManager:null;

        System.out.println("工作项的所有参数！！！=="+workItem.getParameters());

        /**
         * 第一种方法：反射方式调用service方法，bpmn文件种需要将servicetask组件的调用方法进行绑定
         * 获取bpmn各种标签的name属性值
         */
        String service = (String) workItem.getParameter("Interface");
        String interfaceImplementationRef = (String) workItem.getParameter("interfaceImplementationRef");
        String operation = (String) workItem.getParameter("Operation");

        /**
         * 获取传入的参数类型,目前只能传递一个值，bpmn文件的后一个参数类型会覆盖之前的参数类型
         * TODO 想办法获取多个参数类型
         *
         * 如果没有方法获取多个参数类型，那么只能手动在反射时指定类型。而且具体调用几个参数和bpmn文件中的
         * <inMessageRef>标签的个数无关。只和它的数据类型有点关系
         */
        String parameterType = (String) workItem.getParameter("ParameterType");
        Object parameterOne = workItem.getParameter("calca");
        Object parameterTwo = workItem.getParameter("calcb");
        System.out.println("calca="+parameterOne+" calcb="+parameterTwo);

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
                classes = new Class<?>[]{Class.forName(parameterType),Class.forName(parameterType)};

//                classes = new Class<?>[]{Double.class,Class.forName(parameterType)};
//                classes = new Class<?>[]{Double.class,String.class};

                params = new Object[]{parameterOne,parameterTwo};
            }
            Method method = c.getMethod(operation, classes);
            Object result = method.invoke(instance, params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("sum", result);
            workItemManager.completeWorkItem(workItem.getId(), results);
        }catch (Exception e){
            e.printStackTrace();
        }


        /**
         * 2.第二种方法。直接在handler里面进行注入service，只需要获取参数和写入结果，可绑定基本上service的绑定基本没什么关系
         * 需要注入service的时候，创建handler对象时不能直接new，需要容器创建
         */
//        double parameterOne = (double) workItem.getParameter("calca");
//        double parameterTwo = (double) workItem.getParameter("calcb");
//
//        if(calcTwoNumberService==null){
//            System.out.println("service 注入失败,重新new");
//            calcTwoNumberService=new CalcTwoNumberService();
//        }else{
//            System.out.println("service 注入成功");
//        }
//
//        String result=calcTwoNumberService.calcAAndB(parameterOne,parameterTwo);
//        Map<String, Object> results = new HashMap<String, Object>();
//        results.put("sum", result);
//        workItemManager.completeWorkItem(workItem.getId(), results);


    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        this.workItem=workItem!=null?workItem:null;
        this.workItemManager=workItemManager!=null?workItemManager:null;
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
}
