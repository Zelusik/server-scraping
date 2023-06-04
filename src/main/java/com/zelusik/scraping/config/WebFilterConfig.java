package com.zelusik.scraping.config;

import com.zelusik.scraping.logger.filter.LogApiInfoFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;

@Configuration
public class WebFilterConfig {

    /**
     * <p>
     * API 요청/응답에 대한 로그를 출력하는 filter.
     */
    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogApiInfoFilter());
//        filterRegistrationBean.setOrder(-101);    Spring Security 적용 시 주석 해제 필요.
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
