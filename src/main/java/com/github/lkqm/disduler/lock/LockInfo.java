package com.github.lkqm.disduler.lock;

import lombok.Data;

import java.io.Serializable;

/**
 * 锁信息
 */
@Data
public class LockInfo implements Serializable {

    private boolean needLock;

    private String key;

    private Integer expiredSeconds;

}
