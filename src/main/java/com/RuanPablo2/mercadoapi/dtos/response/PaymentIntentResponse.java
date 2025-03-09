package com.RuanPablo2.mercadoapi.dtos.response;

import java.util.HashMap;
import java.util.Map;

public class PaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private long amountInCents;

    public PaymentIntentResponse(String clientSecret, String paymentIntentId, long amountInCents) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.amountInCents = amountInCents;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("clientSecret", clientSecret);
        map.put("paymentIntentId", paymentIntentId);
        map.put("amountInCents", amountInCents);
        return map;
    }
}