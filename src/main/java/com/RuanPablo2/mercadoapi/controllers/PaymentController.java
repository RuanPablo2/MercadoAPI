package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.dtos.response.PaymentIntentResponse;
import com.RuanPablo2.mercadoapi.exception.PaymentException;
import com.RuanPablo2.mercadoapi.security.CustomUserDetails;
import com.RuanPablo2.mercadoapi.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPaymentIntent(
            @RequestParam Long orderId,
            @RequestParam String currency,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntentForOrder(orderId, currency, userDetails);
            return ResponseEntity.ok(response.toMap());
        } catch (PaymentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}