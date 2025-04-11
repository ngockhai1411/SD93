package com.web.api;

import com.web.dto.request.PaymentDto;
import com.web.dto.request.ProductSizeRequest;
import com.web.dto.response.ResponsePayment;
import com.web.entity.ProductSize;
import com.web.entity.User;
import com.web.entity.Voucher;
import com.web.exception.MessageException;
import com.web.gpay.dto.GpayPaymentRequest;
import com.web.gpay.service.GpayService;
import com.web.repository.ProductSizeRepository;
import com.web.servive.VoucherService;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/gpay")
@CrossOrigin
public class GpayApi {

    @Autowired
    private GpayService gpayService;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserUtils userUtils;

    @PostMapping("/urlpayment")
    public ResponsePayment getUrlPayment(@RequestBody PaymentDto paymentDto) {
        String orderId = String.valueOf(System.currentTimeMillis());
        Double totalAmount = 0D;
        for(ProductSizeRequest p : paymentDto.getListProductSize()){
            if(p.getIdProductSize() == null){
                throw new MessageException("id product size require");
            }
            Optional<ProductSize> productSize = productSizeRepository.findById(p.getIdProductSize());
            if(productSize.isEmpty()){
                throw new MessageException("product size: "+p.getIdProductSize()+" not found");
            }
            if(productSize.get().getQuantity() < p.getQuantity()){
                throw new MessageException("product size: "+p.getIdProductSize()+" not enough quantity");
            }
            totalAmount += productSize.get().getProductColor().getProduct().getPrice() * p.getQuantity();
        }
        if(paymentDto.getCodeVoucher() != null){
            Optional<Voucher> voucher = voucherService.findByCode(paymentDto.getCodeVoucher(), totalAmount);
            if(voucher.isPresent()){
                totalAmount = totalAmount - voucher.get().getDiscount();
            }
        }
        Long td = Math.round(totalAmount);
        System.out.println("tong tien: "+td);
        User user = userUtils.getUserWithAuthority();
        GpayPaymentRequest gpayPaymentRequest = new GpayPaymentRequest(orderId, td.longValue(), "Thanh toán gpay","",user.getId().toString(),
                user.getFullname(), "0423534562",user.getEmail(), "Viet Nam","BANK_INTERNATIONAL","{}","IMMEDIATE");

        String paymentUrl = gpayService.createPaymentUrl(gpayPaymentRequest);
        ResponsePayment responsePayment = new ResponsePayment(paymentUrl,orderId,orderId);
        return responsePayment;
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam Map<String, String> params, Model model) {
        model.addAttribute("params", params);
        return "payment/result";
    }

    @PostMapping("/webhook")
    @ResponseBody
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
        boolean isValid = gpayService.verifyWebhookSignature(payload);
        if (isValid) {
            gpayService.processPaymentNotification(payload);
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }


}
