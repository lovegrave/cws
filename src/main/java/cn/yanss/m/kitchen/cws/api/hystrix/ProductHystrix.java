package cn.yanss.m.kitchen.cws.api.hystrix;

import cn.yanss.m.kitchen.cws.api.ProductClient;
import cn.yanss.m.kitchen.cws.entity.request.ProductRequest;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
@Log4j2
public class ProductHystrix implements ProductClient {
    @Override
    public ReturnModel findProduct(ProductRequest productRequest) {
        log.error("product模块--->查询商品接口调用失败");
        return new ReturnModel(500,"调用失败");
    }

    @Override
    public ReturnModel updateProductStatus(ProductRequest productRequest) {
        log.error("product模块--->修改商品状态失败");
        return new ReturnModel(500,"修改失败");
    }

    @Override
    public ReturnModel findStoreList() {
        log.error("product模块--->修改商品状态失败");
        return new ReturnModel(500,"修改失败");
    }

    @Override
    public ReturnModel findStore(Integer storeId) {
        log.error("product模块--->修改商品状态失败");
        return new ReturnModel(500,"修改失败");
    }
}
