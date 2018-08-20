package cn.yanss.m.kitchen.cws.service;

<<<<<<< Updated upstream
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import com.alibaba.fastjson.JSONArray;;
=======
import com.alibaba.fastjson.JSONArray;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
>>>>>>> Stashed changes

import java.io.IOException;

public interface OrderService {

    ReturnModel addOrder(JSONArray orderResponseList) throws IOException;

    ReturnModel reminder(String orderId);

    ReturnModel refund(String orderId);

    ReturnModel cancelRefund(String orderId);
}
