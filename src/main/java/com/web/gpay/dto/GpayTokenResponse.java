package com.web.gpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Thêm dòng này
@AllArgsConstructor // Hoặc thêm để Lombok tạo constructor với tất cả các tham số
public class GpayTokenResponse {
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
        private String token;
        private String expired_at;
    }
}
