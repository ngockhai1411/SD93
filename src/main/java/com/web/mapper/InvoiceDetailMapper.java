package com.web.mapper;

import com.web.dto.request.InvoiceRequest;
import com.web.dto.response.InvoiceDetailResponse;
import com.web.entity.Invoice;
import com.web.entity.InvoiceDetail;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;

@Component
public class InvoiceDetailMapper {


    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ProductMapper productMapper;

    public InvoiceDetailResponse invoiceDetailToResponse(InvoiceDetail invoiceDetail){
        InvoiceDetailResponse response = mapper.map(invoiceDetail, InvoiceDetailResponse.class);
        response.setColorName(invoiceDetail.getProductSize().getProductColor().getColorName());
        response.setLinkImage(invoiceDetail.getProductSize().getProductColor().getProduct().getImageBanner());
        response.setProductName(invoiceDetail.getProductSize().getProductColor().getProduct().getName());
        response.setProductColor(invoiceDetail.getProductSize().getProductColor());
        response.setProduct(productMapper.productToProResponse(invoiceDetail.getProductSize().getProductColor().getProduct()));
        return response;
    }
}
