package com.web.gpay.dto;

import lombok.Data;

@Data
public class BillInforRequest {
     private String merchantCode;
     private String gpayBillId;
     private String merchantOderId;
     private String signature;
}
