package com.thinkerwolf.gamer.core.spring;

import com.thinkerwolf.gamer.common.DefaultObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringObjectFactory extends DefaultObjectFactory {

    private ApplicationContext context;

    private int autowireMode;

    public SpringObjectFactory(ApplicationContext context) {
        this(context, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
    }

    public SpringObjectFactory(ApplicationContext context, int autowireMode) {
        this.context = context;
        this.autowireMode = autowireMode;
    }


    public int getAutowireMode() {
        return autowireMode;
    }

    public void setAutowireMode(int autowireMode) {
        switch (autowireMode) {
            case AutowireCapableBeanFactory.AUTOWIRE_NO:
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_NAME:
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE:
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR:
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT:
                break;
            default:
                throw new RuntimeException("");
        }
        this.autowireMode = autowireMode;
    }

    @Override
    public Object buildObject(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        try {
            return context.getAutowireCapableBeanFactory().createBean(clazz, autowireMode, true);
        } catch (BeansException e) {
            return super.buildObject(clazz);
        }
    }
}
