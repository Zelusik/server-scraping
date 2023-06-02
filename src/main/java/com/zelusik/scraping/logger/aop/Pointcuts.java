package com.zelusik.scraping.logger.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.zelusik.scraping.controller..*(..))")
    public void controllerPoint(){}

    @Pointcut("execution(* com.zelusik.scraping.service..*(..))")
    public void servicePoint(){}

//    @Pointcut("execution(* com.zelusik.scraping.repository..*(..))")
//    public void repositoryPoint(){}
}
