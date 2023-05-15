package com.example.qj.demo.service;

import org.springframework.stereotype.Service;

@Service
public class CalcTwoNumberService {

    /**
     * 计算a*2的值
     * @return
     */
    public String calcAAndB(Double calca){
        Double sum=calca*2;
        String res=String.format("serviceTask:a=[%s],sum=[%s]",calca,sum);
        return res;
    }

    /**
     * 计算a+b的值
     * @return
     */
    public String calcAAndB(Double calca,Double calcb){
        Double sum=calca+calcb;
        String res=String.format("serviceTask:a=[%s],b=[%s],sum=[%s]",calca,calcb,sum);
        return res;
    }

    /**
     * 组合a，b的值
     * @return
     */
    public String calcAAndB(Double calca,String calcb){
        String res=String.format("serviceTask:a=[%s],b=[%s]",calca,calcb);
        return res;
    }


}
