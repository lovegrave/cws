package cn.yanss.m.kitchen.cws.dao;

import cn.yanss.m.kitchen.cws.entity.response.StoreResponse;

import java.util.List;

public interface StoreDao {

    List<Integer> findStoreIds(String sql);

    StoreResponse findStore(String sql);

    void updateStore(StoreResponse storeResponse);
}
