package com.github.lkqm.disduler;

import com.github.lkqm.disduler.lock.DatabaseLock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static com.github.lkqm.disduler.DisdulerProperties.PREFIX;

/**
 * Spring定时任务分布式支持配置
 */
@Data
@ConfigurationProperties(PREFIX)
public class DisdulerProperties implements Serializable {

    public static final String PREFIX = "disduler";

    /**
     * 不同类型实现分布式方式不一样
     */
    private Type type = Type.redis;

    /**
     * 锁前缀
     */
    private String lockKeyPrefix = "disduler";

    /**
     * 锁过期时间
     */
    private Integer lockExpireSeconds = 10;

    /**
     * 表名
     */
    private String tableName = DatabaseLock.DEFAULT_TABLE_NAME;

    public enum Type {
        /**
         * 基于redis的分布式任务锁
         */
        redis,

        /**
         * 基于db的分布式锁
         */
        db
    }

}
