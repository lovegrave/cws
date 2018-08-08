package cn.yanss.m.kitchen.cws.service;

import com.alibaba.fastjson.JSONArray;
import common.returnModel.ReturnModel;

import java.io.IOException;

public interface OrderService {

    ReturnModel addOrder(JSONArray orderResponseList) throws IOException;

}
