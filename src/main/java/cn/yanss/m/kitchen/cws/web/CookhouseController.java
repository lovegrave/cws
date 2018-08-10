package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.entity.cancel.OrderInfoRequest;
import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.ProductRequest;
import cn.yanss.m.kitchen.cws.entity.request.StoreRequest;
import cn.yanss.m.kitchen.cws.service.CookHouseService;
import cn.yanss.m.kitchen.cws.service.StoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.returnModel.ReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/kitchen/api/cookhouse")
public class CookhouseController {

    @Autowired
    private CookHouseService cookhouseService;
    @Autowired
    private StoreService storeService;

    /**
     * 查询用户所有订单 status 2、3、4、5、6、7、99
     *
     * @param orderRequest
     */
    @PostMapping("/findOrderList")
    public ReturnModel findOrderList(@RequestBody OrderRequest orderRequest) {
        return cookhouseService.findOrderList(orderRequest);
    }

    /**
     * 查询订单的所有详细信息
     */
    @RequestMapping("/findOrderDetail")
    public ReturnModel findOrderDetail(String orderId,Integer storeId) {
        return cookhouseService.findOrderDetail(orderId,storeId);


    }

    /**
     * 操作订单(确认接单)/重新接单
     *
     * @param
     */
    @PostMapping("/opt")
    public ReturnModel opt(@RequestBody OrderInfoRequest orderRequest){
        return cookhouseService.opt(orderRequest.getOrderId());
    }
    /**
     * 操作订单异常接口  正式仪器
     *
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping("/orderException")
    public ReturnModel orderException(@RequestBody OrderRequest orderRequest) throws Exception {
        return cookhouseService.orderException(orderRequest.getOrderId());
    }
    /**
     * 放弃取消订单
     * @param orderId
     * @return
     */
    @PostMapping("/abandonCancel")
    public ReturnModel abandonCancel(@RequestParam("orderId") String orderId){
        return cookhouseService.abandonCancel(orderId);
    }
    /**
     * 手动将订单置为已完成
     * @param orderId
     * @return
     */
    @PostMapping("/orderFinish")
    public ReturnModel orderFinish(@RequestParam("orderId") String orderId) throws JsonProcessingException, ExecutionException, InterruptedException {
        return cookhouseService.orderFinish(orderId);
    }
    /**
     * 申请退款 该接口只能是白名单客户端能访问
     * @param orderId
     * @return
     */
    @PostMapping("/applicationForRefund")
    public ReturnModel applicationForRefund(@RequestParam("orderId") String orderId) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        return cookhouseService.applicationForRefund(orderId);
    }
    /**
     * 查询分配店铺id
     */
    @PostMapping("/findStoreId")
    public ReturnModel findStoreId(@RequestBody StoreRequest storeRequest){
        return cookhouseService.findStoreId(storeRequest);
    }
    /**
     * 查询该店铺的所有美食
     */
    @RequestMapping("/findProduct")
    public ReturnModel findProduct(ProductRequest productRequest){
        return null;
    }
    /**
     * 调整是否售罄
     *
     * @param productRequest
     */
    @PostMapping("/updateProductStatus")
    public ReturnModel updateProductStatus(@RequestBody ProductRequest productRequest){
        return null;
    }
    /**
     * 查询订单的状态
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping("/findOrderStatus")
    public ReturnModel findOrderStatus(OrderRequest orderRequest){
        return null;
    }
    /**
     * 手动查询以及测试机的所有查询(实时订单)
     * @param orderRequest
     * @return
     */
    @PostMapping("/findAllOrder")
    public ReturnModel findAllOrder(@RequestBody OrderRequest orderRequest){
        return null;
    }
    @RequestMapping("/findHistoryOrder")
    public ReturnModel findHistoryOrder(Integer storeId){
        return null;
    }

}
