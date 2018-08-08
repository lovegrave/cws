package cn.yanss.m.kitchen.cws.api;


import cn.yanss.m.kitchen.cws.entity.request.ProductRequest;
import common.returnModel.ReturnModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "yanss-product", url = "http://kitchen-product:8080")
public interface ProductClient {

    @PostMapping("api/v1/product/findStoreProduct")
    ReturnModel findProduct(@RequestBody ProductRequest productRequest);

    @PostMapping("api/v1/product/updateStoreProduct")
    ReturnModel updateProductStatus(ProductRequest productRequest);

}
