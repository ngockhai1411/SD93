package com.web.dto.request;

import com.web.entity.Status;
import com.web.entity.UserAddress;
import com.web.enums.PayType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceRequest {

    private PayType payType;

    private String requestIdMomo;

    private String orderIdMomo;

    private Long userAddressId;

    private String voucherCode;

    private String note;

    private String vnpOrderInfo;

    private String urlVnpay;

    private String statusGpay;

    private String merchantOrderId;

    private List<ProductSizeRequest> listProductSize = new ArrayList<>();
}
