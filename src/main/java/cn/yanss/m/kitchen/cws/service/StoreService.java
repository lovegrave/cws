package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.entity.request.StoreRequest;
import cn.yanss.m.kitchen.cws.entity.response.StoreResponse;

import java.util.List;

/**
 * @author hl
 */
public interface StoreService {

    List<Integer> findStoreIds();

    List<StoreResponse> findStoreList(StoreRequest storeRequest);

    /**
     * 查询单个店铺信息
     * @param storeId
     * @return
     */
    StoreResponse findStore(Integer storeId);

    /**
     * 修改店铺信息
     * @param storeResponse
     * @throws Exception
     */
    void updateStore(StoreResponse storeResponse) throws Exception;
}
