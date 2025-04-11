package com.web.gpay.dto;

import lombok.Data;

@Data
public class CallBackRequest {
    private String gpayBillId;
    private String gpay_trans_id;
    private String merchant_order_id;
    private String status;
    private String embed_data;
    private String user_payment_method;
    private String signature;

}
