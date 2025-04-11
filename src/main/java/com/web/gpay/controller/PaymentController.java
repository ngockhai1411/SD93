package com.web.gpay.controller;

import com.web.gpay.dto.GpayPaymentRequest;
import com.web.gpay.service.GpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final GpayService gpayService;

    @GetMapping("/create")
    public String showPaymentForm(Model model) {
        model.addAttribute("payment", new GpayPaymentRequest());
        return "payment/create";
    }

    @PostMapping("/create")
    public String createPayment(@ModelAttribute GpayPaymentRequest payment) {
        payment.setRequestId(String.valueOf(System.currentTimeMillis()));
        payment.setPaymentType("IMMEDIATE");
        payment.setEmbedData("{}");

        String paymentUrl = gpayService.createPaymentUrl(payment);
        if (paymentUrl != null) {
            return "redirect:" + paymentUrl;
        }
        return "redirect:/payment/error";
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

    @GetMapping("/error")
    public String showErrorPage() {
        return "payment/error";
    }
}