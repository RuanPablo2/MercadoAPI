package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.exception.PaymentException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    public PaymentIntent createPaymentIntent(Long amountInCents, String currency) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", currency);
        params.put("payment_method_types", Arrays.asList("card"));
        return PaymentIntent.create(params);
    }

    public Refund refundPayment(String paymentIntentId) throws PaymentException {
        try {
            Map<String, Object> retrieveParams = new HashMap<>();
            retrieveParams.put("expand", Arrays.asList("latest_charge"));
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId, retrieveParams, null);

            Object latestChargeObj = intent.getLatestCharge();
            String chargeId;
            if (latestChargeObj == null) {
                throw new PaymentException("No charge found for PaymentIntent", "STRIPE-001");
            } else if (latestChargeObj instanceof Charge) {
                chargeId = ((Charge) latestChargeObj).getId();
            } else if (latestChargeObj instanceof String) {
                chargeId = (String) latestChargeObj;
            } else {
                throw new PaymentException("Unexpected type for latest_charge", "STRIPE-002");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("charge", chargeId);
            return Refund.create(params);
        } catch (StripeException e) {
            throw new PaymentException("Stripe error: " + e.getMessage(), "STRIPE-003", e);
        }
    }
}