package com.web.gpay.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GpayTokenRequest {
    private String merchantCode;
    private String password;
}
