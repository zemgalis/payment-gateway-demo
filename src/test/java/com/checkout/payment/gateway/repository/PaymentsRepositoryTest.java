package com.checkout.payment.gateway.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentsRepositoryTest {

  private PaymentsRepository paymentsRepository;

  @BeforeEach
  void setUp() {
    paymentsRepository = new PaymentsRepository();
  }

  @Test
  void add_and_get_shouldStoreAndRetrievePaymentSuccessfully() {
    PostPaymentResponse payment = new PostPaymentResponse();
    UUID paymentId = UUID.randomUUID();
    payment.setId(paymentId);
    payment.setAmount(1000); // Add a little dummy data to verify the exact object returns

    paymentsRepository.add(payment);

    Optional<PostPaymentResponse> retrievedPayment = paymentsRepository.get(paymentId);

    assertTrue(retrievedPayment.isPresent(), "Expected to find the payment in the repository");
    assertEquals(paymentId, retrievedPayment.get().getId());
    assertEquals(1000, retrievedPayment.get().getAmount());
  }

  @Test
  void get_whenPaymentDoesNotExist_shouldReturnEmptyOptional() {
    UUID randomId = UUID.randomUUID();

    Optional<PostPaymentResponse> retrievedPayment = paymentsRepository.get(randomId);

    assertFalse(retrievedPayment.isPresent(), "Expected empty Optional for a non-existent ID");
  }

  @Test
  void add_withSameId_shouldOverwriteExistingPayment() {
    UUID sharedId = UUID.randomUUID();

    PostPaymentResponse firstPayment = new PostPaymentResponse();
    firstPayment.setId(sharedId);
    firstPayment.setAmount(100);

    PostPaymentResponse secondPayment = new PostPaymentResponse();
    secondPayment.setId(sharedId);
    secondPayment.setAmount(500);

    paymentsRepository.add(firstPayment);
    paymentsRepository.add(secondPayment); // Should overwrite the first one

    Optional<PostPaymentResponse> retrievedPayment = paymentsRepository.get(sharedId);

    assertTrue(retrievedPayment.isPresent());
    assertEquals(500, retrievedPayment.get().getAmount(), "Expected the repository to retain the latest payment data");
  }
}
