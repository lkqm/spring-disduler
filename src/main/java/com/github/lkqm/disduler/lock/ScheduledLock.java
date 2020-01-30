package com.github.lkqm.disduler.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时任务分布式锁注解控制
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledLock {

    /**
     * 等价: expireSeconds
     */
    int value() default -1;

    /**
     * 锁过期秒数
     */
    int expireSeconds() default -1;

    /**
     * 是否添加分布式锁
     */
    boolean lock() default true;

}
