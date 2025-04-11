package com.web.gpay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.gpay.config.GpayConfig;
import com.web.gpay.dto.GpayPaymentRequest;
import com.web.gpay.dto.GpayPaymentResponse;
import com.web.gpay.dto.GpayTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpayService {
    private final GpayConfig gpayConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String CHARSET = "UTF-8";

    private String accessToken;
    private long tokenExpireTime;

    public String createPaymentUrl(GpayPaymentRequest payment) {
        try {
            if (accessToken == null) {
                getAccessToken();
            }

            String url = gpayConfig.getApiUrl() + "/init-bill";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("merchant_code", gpayConfig.getMerchantCode());
            requestBody.put("request_id", payment.getRequestId());
            requestBody.put("amount", payment.getAmount());
            requestBody.put("title", payment.getTitle());
            requestBody.put("description", payment.getDescription());
            requestBody.put("customer_id", payment.getCustomerId());
            requestBody.put("customer_name", payment.getCustomerName());
            requestBody.put("phone", payment.getPhone());
            requestBody.put("email", payment.getEmail());
            requestBody.put("address", payment.getAddress());
            requestBody.put("callback_url", gpayConfig.getCallbackUrl());
            requestBody.put("webhook_url", gpayConfig.getWebhookUrl());
            requestBody.put("payment_method", payment.getPaymentMethod());
            requestBody.put("embed_data", payment.getEmbedData());
            requestBody.put("payment_type", payment.getPaymentType());

            // Generate signature
            String signature = generateSignature(requestBody);
            requestBody.put("signature", signature);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<GpayPaymentResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    GpayPaymentResponse.class
            );

            if (response.getBody() != null && "200".equals(response.getBody().getMeta().getCode())) {
                return response.getBody().getResponse().getBill_url();
            }

            log.error("Failed to create payment URL: {}", response.getBody());
            return null;
        } catch (Exception e) {
            log.error("Error creating payment URL", e);
            return null;
        }
    }

    private String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        try {
            String tokenUrl = gpayConfig.getApiUrl() + "/authentication/token/create";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("merchant_code", gpayConfig.getMerchantCode());
            requestBody.put("password", gpayConfig.getMerchantPassword());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            log.debug("Requesting new access token for merchant: {}", gpayConfig.getMerchantCode());

            ResponseEntity<GpayTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    GpayTokenResponse.class
            );

            if (response.getBody() != null && "200".equals(response.getBody().getMeta().getCode())) {
                accessToken = response.getBody().getResponse().getToken();

                String expiredAtStr = response.getBody().getResponse().getExpired_at();
                try {
                    tokenExpireTime = Long.parseLong(expiredAtStr) * 1000;
                } catch (NumberFormatException e) {
                    tokenExpireTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
                    log.warn("Could not parse expired_at time, using default 24h expiration");
                }

                log.debug("Successfully obtained new access token, expires at: {}", tokenExpireTime);
                return accessToken;
            }

            String errorMsg = response.getBody() != null ?
                    response.getBody().getMeta().getInternal_msg() : "Unknown error";
            throw new RuntimeException("Failed to get access token: " + errorMsg);

        } catch (Exception e) {
            log.error("Error getting access token", e);
            throw new RuntimeException("Failed to get access token", e);
        }
    }

    private String generateSignature(Map<String, Object> data) {
        try {
            StringBuilder signatureInput = new StringBuilder();
            signatureInput.append("merchant_code=").append(gpayConfig.getMerchantCode())
                    .append("&request_id=").append(data.get("request_id"))
                    .append("&amount=").append(data.get("amount"))
                    .append("&customer_id=").append(data.get("customer_id"))
                    .append("&payment_method=").append(data.get("payment_method"))
                    .append("&embed_data=").append(data.get("embed_data"));

            log.debug("Generating signature for input: {}", signatureInput);

            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            byte[] privateKeyBytes = Base64.getDecoder().decode(gpayConfig.getPrivateKey());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            privateSignature.initSign(privateKey);
            privateSignature.update(signatureInput.toString().getBytes("UTF-8"));

            byte[] signature = privateSignature.sign();
            return Base64.getEncoder().encodeToString(signature);

        } catch (Exception e) {
            log.error("Error generating signature", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    public boolean verifySignature(String data, String signatureStr) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");

            byte[] publicKeyBytes = Base64.getDecoder().decode(gpayConfig.getPublicKey());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            signature.initVerify(publicKey);
            signature.update(data.getBytes("UTF-8"));

            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            boolean isValid = signature.verify(signatureBytes);

            log.debug("Signature verification result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying signature for data: {}", data, e);
            throw new RuntimeException("Failed to verify signature", e);
        }
    }
    public boolean verifyWebhookSignature(Map<String, Object> payload) {
        try {
            String data = objectMapper.writeValueAsString(payload.get("data"));
            String signature = (String) payload.get("signature");
            return verifySignature(data, signature);
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    public void processPaymentNotification(Map<String, Object> payload) {
        // Xử lý thông tin thanh toán
        // Ví dụ: cập nhật trạng thái đơn hàng, gửi email xác nhận, v.v.
        log.info("Processing payment notification: {}", payload);
        // Thêm logic xử lý tại đây
    }
}