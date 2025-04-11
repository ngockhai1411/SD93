package com.web.dto.response;

import com.web.entity.Invoice;
import com.web.entity.Product;
import com.web.entity.ProductColor;
import com.web.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
public class InvoiceDetailResponse {

    private Long id;

    private Integer quantity;

    private Double price;

    private String productName;

    private String linkImage;

    private String colorName;

    private ProductSize productSize;

    private ProductColor productColor;

    private ProductResponse product;

    private Invoice invoice;
}
