package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.dao.StoreDao;
import cn.yanss.m.kitchen.cws.entity.request.StoreRequest;
import cn.yanss.m.kitchen.cws.entity.response.StoreResponse;
import cn.yanss.m.kitchen.cws.service.StoreService;
import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private RedisService redisService;

    @Override
    public List<Integer> findStoreIds(){
        List<Integer> ids = null;
        String str = redisService.getString("storeIds");
        if(null == str){
            log.info("从数据库获取数据");
            String sql = "select store_id from tb_store";
            ids  = storeDao.findStoreIds(sql);
            redisService.setString("storeIds",JSONObject.toJSONString(ids));
            return ids;
        }
        ids = JSONObject.parseArray(str,Integer.class);
       return ids;
    }

    @Override
    public List<StoreResponse> findStoreList(StoreRequest storeRequest) {
        return Collections.emptyList();
    }

    @Override
    public StoreResponse findStore(Integer storeId) {
        StoreResponse storeResponse = redisService.getObject(OrderStatus.STORERESPONSE+storeId,StoreResponse.class);
        if(null == storeResponse){
            String sql = "select * from tb_store where store_id ="+storeId;
            storeResponse = storeDao.findStore(sql);
            if(null != storeResponse){
                redisService.setObject(OrderStatus.STORERESPONSE+storeId,storeResponse,72000);
            }
        }
        return storeResponse;
    }

    @Override
    public void updateStore(StoreResponse storeResponse) {
        redisService.remove(OrderStatus.STORERESPONSE+storeResponse.getStoreId());
        storeDao.updateStore(storeResponse);
    }
}
