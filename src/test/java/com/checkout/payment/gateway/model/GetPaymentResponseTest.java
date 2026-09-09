package com.checkout.payment.gateway.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetPaymentResponseTest {

  @Test
  void shouldSetAndGetPropertiesCorrectly() {
    GetPaymentResponse response = new GetPaymentResponse();
    UUID testId = UUID.randomUUID();

    response.setId(testId);
    response.setStatus(PaymentStatus.AUTHORIZED);
    response.setCardNumberLastFour(4321);
    response.setExpiryMonth(9);
    response.setExpiryYear(2028);
    response.setCurrency("GBP");
    response.setAmount(1050);

    assertEquals(testId, response.getId());
    assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
    assertEquals(4321, response.getCardNumberLastFour());
    assertEquals(9, response.getExpiryMonth());
    assertEquals(2028, response.getExpiryYear());
    assertEquals("GBP", response.getCurrency());
    assertEquals(1050, response.getAmount());
  }

  @Test
  void toString_shouldReturnFormattedString() {
    GetPaymentResponse response = new GetPaymentResponse();
    UUID testId = UUID.randomUUID();

    response.setId(testId);
    response.setStatus(PaymentStatus.DECLINED);
    response.setCardNumberLastFour(1111);
    response.setExpiryMonth(3);
    response.setExpiryYear(2026);
    response.setCurrency("JPY");
    response.setAmount(5000);

    String result = response.toString();

    assertNotNull(result);
    assertTrue(result.contains(testId.toString()));
    assertTrue(result.contains("status=" + PaymentStatus.DECLINED));
    assertTrue(result.contains("cardNumberLastFour=1111"));
    assertTrue(result.contains("expiryMonth=3"));
    assertTrue(result.contains("expiryYear=2026"));
    assertTrue(result.contains("currency='JPY'"));
    assertTrue(result.contains("amount=5000"));
    assertTrue(result.startsWith("GetPaymentResponse{"));
  }
}
