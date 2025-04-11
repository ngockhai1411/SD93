package com.web.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayType {

    PAYMENT_MOMO,
    PAYMENT_DELIVERY,
    PAYMENT_VNPAY,
    PAYMENT_GPAY,
    PAY_COUNTER;

}
