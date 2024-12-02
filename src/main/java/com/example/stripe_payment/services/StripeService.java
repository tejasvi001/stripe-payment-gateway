package com.example.stripe_payment.services;

import com.example.stripe_payment.dtos.ProductRequest;
import com.example.stripe_payment.dtos.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    /**
     * Creates a Stripe payment session for the given product request.
     *
     * @param productRequest The product details.
     * @return A StripeResponse object containing session details or an error message.
     */
    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        // Set the Stripe secret API key
        Stripe.apiKey = secretKey;

        // Build the product data
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        // Build the price data
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() == null ? "USD" : productRequest.getCurrency())
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        // Build the line item
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        // Create the session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success") // Adjust the URLs as needed
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .build();

        // Create the session
        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            // Log the exception and return a failure response
            System.out.println(e.getMessage());
            return StripeResponse.builder()
                    .status("FAILED")
                    .message("Failed to create payment session: " + e.getMessage())
                    .build();
        }

        // Build and return a successful response
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created successfully")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
