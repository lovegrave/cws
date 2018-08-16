package cn.yanss.m.kitchen.cws;

import cn.yanss.m.kitchen.cws.entity.response.StoreResponse;
import cn.yanss.m.kitchen.cws.service.StoreService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CwsApplicationTests {

    @Autowired
    private StoreService storeService;
    @Test
    public void contextLoads() {

        List<Integer> ids = storeService.findStoreIds();
        System.err.println(ids.toString());
    }

    @Test
    public void contextLoads1() {

        StoreResponse store = storeService.findStore(7);
        System.err.println(JSON.toJSONString(store));
    }

}
