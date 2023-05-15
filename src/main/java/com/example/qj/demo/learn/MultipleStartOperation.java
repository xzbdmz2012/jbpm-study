package com.example.qj.demo.learn;

import com.example.qj.demo.listener.DefaultCountDownProcessEventListener;
import com.example.qj.demo.util.KieBaseAndKieSessionFactory;
import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.audit.event.RuleFlowLogEvent;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import static org.assertj.core.api.Assertions.assertThat;

public class MultipleStartOperation {

    /**
     * 测试多重开始事件，它没有自己的专属标签，是其他开始事件的组合，经常和排他网关组合使用
     * 流程节点的路径标签可以不写，比如<outgoing><imcoming>,但是<sequenceFlow>必须要写
     * 空开始事件只能由ksession.startProcess()启动，排他网关一次只能有一个事件通过
     * 定时开始事件，设置特殊的定时器数值，它的回调函数可能会比主函数晚执行
     * @throws Exception
     */
    @Test(timeout=10000)
    public void multipleStartTest() throws Exception {

        DefaultCountDownProcessEventListener countDownListener = new DefaultCountDownProcessEventListener(10);
        KieBase kbase = KieBaseAndKieSessionFactory.createKnowledgeBase("multipleStartOperation.bpmn2");
        KieSession ksession = KieBaseAndKieSessionFactory.createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);

        //从session中获取工作内存日志
        WorkingMemoryInMemoryLogger logger = new WorkingMemoryInMemoryLogger(ksession);;

        ksession.startProcess("multipleStart");
        countDownListener.waitTillCompleted();
        assertThat(getNumberOfProcessInstances("multipleStart",logger)).isEqualTo(2);
        System.out.println("测试多重开始事件完成");
    }

    /**
     * 根据流程ID和工作内存日志对象获取流程的个数
     * @param processId
     * @param logger
     * @return
     */
    public int getNumberOfProcessInstances(String processId,WorkingMemoryInMemoryLogger logger) {
        int counter = 0;

        LogEvent[] events = logger.getLogEvents().toArray(new LogEvent[0]);
        for (LogEvent event : events ) {
            if (event.getType() == LogEvent.BEFORE_RULEFLOW_CREATED) {
                if(((RuleFlowLogEvent) event).getProcessId().equals(processId)) {
                    counter++;
                }
            }
        }
        return counter;
    }



}
