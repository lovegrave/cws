package cn.yanss.m.kitchen.cws.api;


import cn.yanss.m.kitchen.cws.api.config.FeignConfig;
import cn.yanss.m.kitchen.cws.api.hystrix.ProductHystrix;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.entity.request.ProductRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "yanss-product", url = "http://kitchen-product:8080",configuration = FeignConfig.class,fallback = ProductHystrix.class)
public interface ProductClient {

    @PostMapping("api/v1/product/findStoreProduct")
    ReturnModel findProduct(@RequestBody ProductRequest productRequest);

    @PostMapping("api/v1/product/updateStoreProduct")
    ReturnModel updateProductStatus(ProductRequest productRequest);

    @GetMapping("api/v1/product/findStoreList")
    ReturnModel findStoreList();

    @PostMapping("api/v1/product/findStore")
    ReturnModel findStore(@RequestParam("storeId") Integer storeId);

}
