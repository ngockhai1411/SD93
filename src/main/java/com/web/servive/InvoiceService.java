package com.web.servive;

import com.web.dto.request.InvoiceRequest;
import com.web.dto.request.InvoiceRequestCounter;
import com.web.dto.request.ProductSizeRequest;
import com.web.dto.response.InvoiceResponse;
import com.web.enums.PayType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public interface InvoiceService {
    
    public InvoiceResponse create(InvoiceRequest invoiceRequest);

    public InvoiceResponse updateStatus(Long invoiceId, Long statusId);

    public List<InvoiceResponse> findByUser();

    public Page<InvoiceResponse> findAll(Date from, Date to, Pageable pageable);

    public InvoiceResponse cancelInvoice(Long invoiceId);

    public InvoiceResponse findById(Long invoiceId);

    public InvoiceResponse findByIdForAdmin(Long invoiceId);

    public Page<InvoiceResponse> findAllFull(Date from, Date to, PayType payType, Long statusId, Pageable pageable);

    Long payCounter(InvoiceRequestCounter invoiceRequestCounter);
}
