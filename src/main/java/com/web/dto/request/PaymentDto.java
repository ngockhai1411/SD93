package com.web.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaymentDto {
//    private Long amount;
    private String codeVoucher;
    private String content;
    private String returnUrl;
    private String notifyUrl;
    private List<ProductSizeRequest> listProductSize = new ArrayList<>();
}

