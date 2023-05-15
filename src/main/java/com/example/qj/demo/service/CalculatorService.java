package com.example.qj.demo.service;

public class CalculatorService {

    public String addOperation(Double firstAddend){
        Double sum=firstAddend*2;
        String res=String.format("serviceTask:firstAddend=[%s],sum=[%s]",firstAddend,sum);
        return res;
    }

}