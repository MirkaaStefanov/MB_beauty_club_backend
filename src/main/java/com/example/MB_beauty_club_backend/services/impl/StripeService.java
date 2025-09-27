package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.dto.OrderProductDTO;
import com.example.MB_beauty_club_backend.models.dto.PaymentRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${server.frontend.baseUrl}")
    private String frontendBaseUrl;

    public String createCheckoutSession(PaymentRequest request) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (OrderProductDTO item : request.getItems()) {

            // --- FIX 1: Correctly calculate price in cents using BigDecimal for precision ---
            // Example: 10.55 EUR * 100 = 1055 cents
            long unitAmountCents = item.getPrice()
                    .multiply(new BigDecimal("100"))
                    .setScale(0, RoundingMode.HALF_UP) // Round to nearest whole cent
                    .longValue();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("bgn")
                                    .setUnitAmount(unitAmountCents) // Use the precise cent value
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getProduct().getName())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Add customer's email if available for a smoother checkout (optional but recommended)
                // .setCustomerEmail("customer@example.com")
                .setSuccessUrl(frontendBaseUrl + "/orders/payment-success/" + request.getOrderId())
                .setCancelUrl(frontendBaseUrl + "/orders/payment-cancel/" + request.getOrderId())
                .build();

        // --- FIX 2: Correctly calling Session.create() with the params object ---
        Session session = Session.create(params);
        return session.getUrl();
    }

}
