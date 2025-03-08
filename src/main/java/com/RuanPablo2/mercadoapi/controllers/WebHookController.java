package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import com.RuanPablo2.mercadoapi.services.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Scanner;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/api/webhooks")
public class WebHookController {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(HttpServletRequest request,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        String payload;
        try (Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A")) {
            payload = s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading payload");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (intent != null) {
                System.out.println("PaymentIntent ID from webhook: " + intent.getId());
                orderService.updateOrderPaymentStatus(intent.getId(), OrderStatus.PAID);
            }
        } else if ("charge.refunded".equals(event.getType())) {
            Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
            if (charge != null && charge.getPaymentIntent() != null) {
                System.out.println("PaymentIntent ID from refunded event: " + charge.getPaymentIntent());
                orderService.updateOrderPaymentStatus(charge.getPaymentIntent(), OrderStatus.REFUNDED);
            }
        }
        return ResponseEntity.ok("Webhook processed");
    }
}