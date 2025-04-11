package com.web.gpay.dto;

import lombok.Data;

@Data
public class BillInforResponse {
    private Meta meta;
    private Response response;
    @Data
    public static class Meta{
        private String code;
        private String msg;
        private String internal_msg;
    }
    @Data
    public static class Response{
        private String embed_data;
        private String gpay_trans_id;
        private String gpay_bill_id;
        private String merchant_order_id;
        private String status;
        private String user_payment_method;
        private String signature;
    }
}
