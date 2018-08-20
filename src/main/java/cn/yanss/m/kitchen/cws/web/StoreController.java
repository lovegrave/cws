package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kitchen/api/store")
public class StoreController {
    @Autowired
    private StoreService storeService;

    @RequestMapping("/findStoreIds")
    public ReturnModel findStoreIds(){
        return new ReturnModel(storeService.findStoreIds());
    }

    @RequestMapping("/findStore")
    public ReturnModel findStore(Integer storeId){
        return new ReturnModel(storeService.findStore(storeId));
    }
}
