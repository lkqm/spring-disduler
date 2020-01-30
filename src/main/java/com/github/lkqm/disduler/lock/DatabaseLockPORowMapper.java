package com.github.lkqm.disduler.lock;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseLockPORowMapper implements RowMapper<DatabaseLockPO> {

    public static final DatabaseLockPORowMapper INSTANCE = new DatabaseLockPORowMapper();

    @Override
    public DatabaseLockPO mapRow(ResultSet resultSet, int i) throws SQLException {
        DatabaseLockPO entity = new DatabaseLockPO();
        entity.setKey(resultSet.getString("key"));
        entity.setData(resultSet.getString("data"));
        entity.setLockTimestamp(resultSet.getLong("lock_timestamp"));
        entity.setLockAutoExpiredTimestamp(resultSet.getLong("lock_auto_expired_timestamp"));
        return entity;
    }
}