package com.web.serviceImp;

import com.web.dto.response.ProductSizeResponse;
import com.web.entity.ProductSize;
import com.web.repository.ProductSizeRepository;
import com.web.servive.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductSizeServiceImp implements ProductSizeService {

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Override
    public ProductSizeResponse findById(Long id) {
        ProductSize productSize = productSizeRepository.findById(id).get();
        ProductSizeResponse productSizeResponse = new ProductSizeResponse(productSize, productSize.getProductColor(), productSize.getProductColor().getProduct());
        return productSizeResponse;
    }
}
