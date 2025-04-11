package com.web.gpay.dto;

import lombok.Data;

@Data
public class GpayPaymentResponse {
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
        private String bill_url;
        private String bill_id;
        private String request_id;
        private String expired_time;
        private String signature;
    }

}
