package com.checkout.payment.gateway.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PostPaymentResponseTest {

  @Test
  void shouldSetAndGetPropertiesCorrectly() {
    PostPaymentResponse response = new PostPaymentResponse();
    UUID testId = UUID.randomUUID();

    response.setId(testId);
    response.setStatus(PaymentStatus.AUTHORIZED);
    response.setCardNumberLastFour(8877);
    response.setExpiryMonth(12);
    response.setExpiryYear(2027);
    response.setCurrency("USD");
    response.setAmount(1500);

    assertEquals(testId, response.getId());
    assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
    assertEquals(8877, response.getCardNumberLastFour());
    assertEquals(12, response.getExpiryMonth());
    assertEquals(2027, response.getExpiryYear());
    assertEquals("USD", response.getCurrency());
    assertEquals(1500, response.getAmount());
  }

  @Test
  void toString_shouldReturnFormattedString() {
    PostPaymentResponse response = new PostPaymentResponse();
    UUID testId = UUID.randomUUID();

    response.setId(testId);
    response.setStatus(PaymentStatus.DECLINED);
    response.setCardNumberLastFour(1234);
    response.setExpiryMonth(4);
    response.setExpiryYear(2025);
    response.setCurrency("EUR");
    response.setAmount(200);

    String result = response.toString();

    assertNotNull(result);
    assertTrue(result.contains(testId.toString()));
    assertTrue(result.contains("status=" + PaymentStatus.DECLINED));
    assertTrue(result.contains("cardNumberLastFour=1234"));
    assertTrue(result.contains("expiryMonth=4"));
    assertTrue(result.contains("expiryYear=2025"));
    assertTrue(result.contains("currency='EUR'"));
    assertTrue(result.contains("amount=200"));

    assertTrue(result.startsWith("PostPaymentResponse{"));
  }
}
