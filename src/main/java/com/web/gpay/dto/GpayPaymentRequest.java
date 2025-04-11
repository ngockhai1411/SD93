package com.web.gpay.dto;

import lombok.Data;

@Data
public class GpayPaymentRequest {
    private String requestId;
    private long amount;
    private String title;
    private String description;
    private String customerId;
    private String customerName;
    private String phone;
    private String email;
    private String address;
    private String paymentMethod;
    private String embedData;
    private String paymentType;

    public GpayPaymentRequest() {

    }


    public GpayPaymentRequest(String requestId, long amount, String title, String description, String customerId, String customerName, String phone, String email, String address, String paymentMethod, String embedData, String paymentType) {
        this.requestId = requestId;
        this.amount = amount;
        this.title = title;
        this.description = description;
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.embedData = embedData;
        this.paymentType = paymentType;
    }
}