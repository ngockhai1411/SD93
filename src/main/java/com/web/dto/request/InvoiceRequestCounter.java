package com.web.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Getter
@Setter
public class InvoiceRequestCounter {

    private List<ProductSizeRequest> listProductSize;

    private String fullName;

    private String phone;

    private Long voucherId;
}
