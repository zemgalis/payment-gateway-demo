package com.checkout.payment.gateway.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostPaymentRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  private PostPaymentRequest createValidRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("1234567890123456");
    request.setExpiryMonth(4);
    request.setExpiryYear(2027);
    request.setCurrency("USD");
    request.setAmount(1500);
    request.setCvv("123");
    return request;
  }

  @Test
  void validRequest_shouldPassAllValidation() {
    PostPaymentRequest request = createValidRequest();
    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty(), "Expected no validation errors for a fully valid request");
  }

  @Test
  void getExpiryDate_withSingleDigitMonth_shouldFormatWithLeadingZero() {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryMonth(4);
    request.setExpiryYear(2026);

    assertEquals("04/2026", request.getExpiryDate());
  }

  @Test
  void getExpiryDate_withDoubleDigitMonth_shouldFormatCorrectly() {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryMonth(11);
    request.setExpiryYear(2027);

    assertEquals("11/2027", request.getExpiryDate());
  }

  @Test
  void invalidCardNumber_tooShort_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setCardNumber("1234567890123");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCardNumber_tooLong_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setCardNumber("12345678901234567890");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCardNumber_containsLetters_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setCardNumber("123456789012345A");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCardNumber_nullOrEmpty_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setCardNumber(null);
    assertFalse(validator.validate(request).isEmpty());

    request.setCardNumber("");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidExpiryMonth_outOfBounds_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setExpiryMonth(0);
    assertFalse(validator.validate(request).isEmpty());

    request.setExpiryMonth(13);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidExpiryYear_inThePast_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryYear(2025);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCurrency_notInAllowedList_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setCurrency("GBP");
    assertFalse(validator.validate(request).isEmpty());

    request.setCurrency("usd");
    assertFalse(validator.validate(request).isEmpty());

    request.setCurrency("US");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCurrency_nullOrEmpty_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setCurrency(null);
    assertFalse(validator.validate(request).isEmpty());

    request.setCurrency("");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidAmount_zeroOrNegative_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setAmount(0);
    assertFalse(validator.validate(request).isEmpty());

    request.setAmount(-100);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCvv_tooShort_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setCvv("12");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCvv_tooLong_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setCvv("12345");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCvv_containsLetters_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();
    request.setCvv("12A");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void invalidCvv_nullOrEmpty_shouldFailValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setCvv(null);
    assertFalse(validator.validate(request).isEmpty());

    request.setCvv("");
    assertFalse(validator.validate(request).isEmpty());
  }
}
