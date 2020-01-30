package com.github.lkqm.disduler.lock;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 */
public class DatabaseLock implements Lock {

    private String table;
    private JdbcTemplate jdbcTemplate;

    public static final String DEFAULT_TABLE_NAME = "disduler_lock";

    public DatabaseLock(JdbcTemplate jdbcTemplate) {
        this(DEFAULT_TABLE_NAME, jdbcTemplate);
    }

    public DatabaseLock(String table, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        if (table == null || table.trim().length() == 0) {
            table = DEFAULT_TABLE_NAME;
        }
        this.table = table;
    }

    @Override
    public boolean lock(String key, String value, int expiredSeconds) {
        String querySql = "select `key`, `data`, `lock_timestamp`, `lock_auto_expired_timestamp`" +
                " from `%s` where `key` = ?";
        querySql = formatSqlWithTable(querySql);
        DatabaseLockPO lock = null;
        try {
            lock = jdbcTemplate.queryForObject(querySql, DatabaseLockPORowMapper.INSTANCE, key);
        } catch (EmptyResultDataAccessException e) {
        }

        long current = System.currentTimeMillis();
        if (lock != null && lock.getLockAutoExpiredTimestamp() > current) return false;

        DatabaseLockPO lockToSave = new DatabaseLockPO();
        lockToSave.setKey(key);
        lockToSave.setData(value);
        lockToSave.setLockTimestamp(current);
        lockToSave.setLockAutoExpiredTimestamp(current + TimeUnit.SECONDS.toMillis(expiredSeconds));

        // 新记录
        if (lock == null) {
            String insertSql = "insert into `%s`(`key`, `data`, `lock_timestamp`, `lock_auto_expired_timestamp`)" +
                    " values(?, ?, ?, ?)";
            insertSql = formatSqlWithTable(insertSql);
            try {
                jdbcTemplate.update(insertSql, lockToSave.getKey(), lockToSave.getData(),
                        lockToSave.getLockTimestamp(), lockToSave.getLockAutoExpiredTimestamp());
                return true;
            } catch (DuplicateKeyException e) {
                return false;
            }
        }

        // 修改
        String updateSql = "update `%s`" +
                " set `data` = ?, `lock_timestamp`=?, `lock_auto_expired_timestamp`=?" +
                " where `key` = ? and `lock_auto_expired_timestamp` <= ?";
        updateSql = formatSqlWithTable(updateSql);
        int rows = jdbcTemplate.update(updateSql, lockToSave.getData(), lockToSave.getLockTimestamp(),
                lockToSave.getLockAutoExpiredTimestamp(), lockToSave.getKey(), current);
        return rows > 0;
    }

    @Override
    public boolean release(String key, String value) {
        String deleteSql = "delete from `%s` where `key` = ? and `data` = ?";
        deleteSql = formatSqlWithTable(deleteSql);
        int rows = jdbcTemplate.update(deleteSql, key, value);
        return rows > 0;
    }

    private String formatSqlWithTable(String tplSql) {
        return String.format(tplSql, table);
    }

}
