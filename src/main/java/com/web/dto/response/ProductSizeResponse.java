package com.web.dto.response;

import com.web.entity.Product;
import com.web.entity.ProductColor;
import com.web.entity.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeResponse {

    private ProductSize productSize;

    private ProductColor productColor;

    private Product product;
}
