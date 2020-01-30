package com.github.lkqm.disduler.lock;

/**
 * 分布式锁
 */
public interface Lock {

    /**
     * 锁定资源
     */
    boolean lock(String key, String value, int expiredSeconds);

    /**
     * 释放锁资源
     */
    boolean release(String key, String value);

}
