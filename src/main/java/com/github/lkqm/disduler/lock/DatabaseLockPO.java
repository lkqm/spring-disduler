package com.github.lkqm.disduler.lock;

import lombok.Data;

import java.io.Serializable;

/**
 * 基于数据库的锁实体
 */
@Data
public class DatabaseLockPO implements Serializable {

    private String key;

    private String data;

    private Long lockTimestamp;

    private Long lockAutoExpiredTimestamp;

}