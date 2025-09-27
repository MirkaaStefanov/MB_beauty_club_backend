package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.models.dto.PaymentRequest;
import com.example.MB_beauty_club_backend.services.impl.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;

    @PostMapping("/create-session")
    public String createCheckoutSession(@RequestBody PaymentRequest request, @RequestHeader("Authorization") String auth) throws StripeException {
        return stripeService.createCheckoutSession(request);


    }

}
