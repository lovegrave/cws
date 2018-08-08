package cn.yanss.m.kitchen.cws.dao.impl;

import cn.yanss.m.kitchen.cws.dao.StoreDao;
import cn.yanss.m.kitchen.cws.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StoreDaoImpl implements StoreDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
}
