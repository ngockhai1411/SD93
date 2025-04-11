package com.web.dto.response;

import com.web.entity.InvoiceStatus;
import com.web.entity.Status;
import com.web.entity.UserAddress;
import com.web.entity.Voucher;
import com.web.enums.PayType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceResponse {

    private Long id;

    private Date createdDate;

    private Time createdTime;

    private Double totalAmount;

    private String receiverName;

    private String phone;

    private String note;

    private String address;

    private PayType payType;

    private UserAdressResponse userAddress;

    private Status status;

    private Voucher voucher;

    private List<InvoiceStatus> invoiceStatuses = new ArrayList<>();
}
