package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.model.bank.BankPaymentRequest;
import com.checkout.payment.gateway.model.bank.BankPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentGatewayServiceTest {

  private PaymentsRepository paymentsRepository;
  private BankSimulatorClient bankSimulatorClient;
  private PaymentGatewayService paymentGatewayService;

  @BeforeEach
  void setUp() {
    paymentsRepository = mock(PaymentsRepository.class);
    bankSimulatorClient = mock(BankSimulatorClient.class);
    paymentGatewayService = new PaymentGatewayService(paymentsRepository, bankSimulatorClient);
  }

  @Test
  void getPaymentById_whenPaymentExists_shouldReturnPayment() {
    UUID testId = UUID.randomUUID();
    PostPaymentResponse expectedPayment = new PostPaymentResponse();
    expectedPayment.setId(testId);

    when(paymentsRepository.get(testId)).thenReturn(Optional.of(expectedPayment));

    PostPaymentResponse actualPayment = paymentGatewayService.getPaymentById(testId);

    assertEquals(testId, actualPayment.getId());
  }

  @Test
  void getPaymentById_whenPaymentDoesNotExist_shouldThrowException() {
    UUID testId = UUID.randomUUID();
    when(paymentsRepository.get(testId)).thenReturn(Optional.empty());

    assertThrows(EventProcessingException.class, () -> paymentGatewayService.getPaymentById(testId));
  }

  private PostPaymentRequest createBaseRequest(int expiryYear) {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("1234567890123456");
    request.setExpiryMonth(12);
    request.setExpiryYear(expiryYear);
    request.setCurrency("USD");
    request.setAmount(1000);
    request.setCvv("123");
    return request;
  }

  @Test
  void processPayment_whenExpiryInPast_shouldRejectAndNotCallBank() {
    PostPaymentRequest request = createBaseRequest(2020);

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertEquals(PaymentStatus.REJECTED, response.getStatus());
    assertEquals(3456, response.getCardNumberLastFour(), "Expected card number to be masked correctly");

    verify(bankSimulatorClient, never()).processTransaction(any(BankPaymentRequest.class));
    verify(paymentsRepository).add(any(PostPaymentResponse.class));
  }

  @Test
  void processPayment_whenBankAuthorizes_shouldReturnAuthorizedStatus() {
    PostPaymentRequest request = createBaseRequest(2030); // Future date

    BankPaymentResponse mockBankResponse = new BankPaymentResponse();
    mockBankResponse.setAuthorized(true);
    when(bankSimulatorClient.processTransaction(any(BankPaymentRequest.class))).thenReturn(mockBankResponse);

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertNotNull(response.getId());
    assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
    assertEquals(3456, response.getCardNumberLastFour());

    verify(bankSimulatorClient).processTransaction(any(BankPaymentRequest.class));
    verify(paymentsRepository).add(any(PostPaymentResponse.class));
  }

  @Test
  void processPayment_whenBankDeclines_shouldReturnDeclinedStatus() {
    PostPaymentRequest request = createBaseRequest(2030); // Future date

    BankPaymentResponse mockBankResponse = new BankPaymentResponse();
    mockBankResponse.setAuthorized(false); // Simulate bank decline
    when(bankSimulatorClient.processTransaction(any(BankPaymentRequest.class))).thenReturn(mockBankResponse);

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertNotNull(response.getId());
    assertEquals(PaymentStatus.DECLINED, response.getStatus());
    assertEquals(3456, response.getCardNumberLastFour());

    verify(bankSimulatorClient).processTransaction(any(BankPaymentRequest.class));
    verify(paymentsRepository).add(any(PostPaymentResponse.class));
  }
}
