package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import com.alibaba.fastjson.JSONArray;;

import java.io.IOException;

public interface OrderService {

    ReturnModel addOrder(JSONArray orderResponseList) throws IOException;

}
