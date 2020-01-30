package com.github.lkqm.disduler;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动分布式定时任务支持
 *
 * @see DisdulerConfiguration 对应加载的配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DisdulerConfiguration.class)
public @interface EnableDisduler {
}
