package com.web.api;

import com.web.dto.request.CategoryRequest;
import com.web.dto.request.InvoiceRequest;
import com.web.dto.request.InvoiceRequestCounter;
import com.web.dto.request.ProductSizeRequest;
import com.web.dto.response.InvoiceResponse;
import com.web.entity.Category;
import com.web.enums.PayType;
import com.web.servive.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin
public class InvoiceApi {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/user/create")
    public ResponseEntity<?> save(@RequestBody InvoiceRequest invoiceRequest){
        InvoiceResponse result = invoiceService.create(invoiceRequest);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/admin/pay-counter")
    public ResponseEntity<?> payCounter(@RequestBody InvoiceRequestCounter invoiceRequestCounter){
        Long id = invoiceService.payCounter(invoiceRequestCounter);
        return new ResponseEntity<>(id,HttpStatus.CREATED);
    }

    @PostMapping("/admin/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam("idInvoice") Long idInvoice, @RequestParam("idStatus") Long idStatus){
        InvoiceResponse result = invoiceService.updateStatus(idInvoice, idStatus);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/user/cancel-invoice")
    public ResponseEntity<?> cancelInvoice(@RequestParam("idInvoice") Long idInvoice){
        InvoiceResponse result = invoiceService.cancelInvoice(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/user/find-by-user")
    public ResponseEntity<?> findByUser(){
        List<InvoiceResponse> result = invoiceService.findByUser();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/find-all")
    public ResponseEntity<?> findAll(@RequestParam(value = "from",required = false) Date from,
                                     @RequestParam(value = "to",required = false) Date to,
                                     @RequestParam(value = "paytype",required = false) PayType payType,
                                     @RequestParam(value = "status",required = false) Long statusId, Pageable pageable){
        Page<InvoiceResponse> result = invoiceService.findAllFull(from, to,payType, statusId,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/find-all-by-date")
    public ResponseEntity<?> findAllByDate(@RequestParam(value = "from",required = false) Date from,
                                     @RequestParam(value = "to",required = false) Date to, Pageable pageable){
        Page<InvoiceResponse> result = invoiceService.findAll(from, to,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/user/find-by-id")
    public ResponseEntity<?> findByUser(@RequestParam("idInvoice") Long idInvoice){
        InvoiceResponse result = invoiceService.findById(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/find-by-id")
    public ResponseEntity<?> findByAdmin(@RequestParam("idInvoice") Long idInvoice){
        InvoiceResponse result = invoiceService.findByIdForAdmin(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
