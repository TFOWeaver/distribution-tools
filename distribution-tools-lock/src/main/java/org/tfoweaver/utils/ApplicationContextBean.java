package org.tfoweaver.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @title: ApplicationContextBean
 * @Author Star_Chen
 * @Date: 2022/7/18 14:49
 * @Version 1.0
 */
@Component
public class ApplicationContextBean implements ApplicationContextAware, InitializingBean {


    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println("==============>" + beanDefinitionName);
        }
    }
}
