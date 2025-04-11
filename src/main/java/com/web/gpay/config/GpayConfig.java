package com.web.gpay.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GpayConfig {
    @Value("${gpay.sandbox.url}")
    private String apiUrl;

    @Value("${gpay.merchant.code}")
    private String merchantCode;

    @Value("${gpay.merchant.password}")
    private String merchantPassword;

    @Value("${gpay.callback.url}")
    private String callbackUrl;

    @Value("${gpay.webhook.url}")
    private String webhookUrl;

    @Value("${gpay.private.key}")
    private String privateKey;

    @Value("${gpay.public.key}")
    private String publicKey;
}