package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.dao.StoreDao;
import cn.yanss.m.kitchen.cws.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreDao storeDao;
}
