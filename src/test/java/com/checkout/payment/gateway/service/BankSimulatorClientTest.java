package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.model.bank.BankPaymentRequest;
import com.checkout.payment.gateway.model.bank.BankPaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

class BankSimulatorClientTest {

  private RestTemplate restTemplate;
  private BankSimulatorClient bankSimulatorClient;
  private final String mockBankUrl = "http://localhost:8080/payments";

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    bankSimulatorClient = new BankSimulatorClient(restTemplate, mockBankUrl);
  }

  @Test
  void processTransaction_whenSuccessful_shouldReturnAuthorizedResponse() {
    BankPaymentRequest request = new BankPaymentRequest();
    request.setAmount(1000);

    BankPaymentResponse expectedResponse = new BankPaymentResponse();
    expectedResponse.setAuthorized(true);
    expectedResponse.setAuthorizationCode("auth-123");

    ResponseEntity<BankPaymentResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

    when(restTemplate.postForEntity(eq(mockBankUrl), any(HttpEntity.class), eq(BankPaymentResponse.class)))
        .thenReturn(responseEntity);

    BankPaymentResponse actualResponse = bankSimulatorClient.processTransaction(request);

    assertTrue(actualResponse.isAuthorized(), "Expected the client to map a 200 OK to an authorized response");
  }

  @Test
  void processTransaction_when503ServiceUnavailable_shouldReturnDeclinedResponse() {
    BankPaymentRequest request = new BankPaymentRequest();

    when(restTemplate.postForEntity(eq(mockBankUrl), any(HttpEntity.class), eq(BankPaymentResponse.class)))
        .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

    BankPaymentResponse actualResponse = bankSimulatorClient.processTransaction(request);

    assertFalse(actualResponse.isAuthorized(), "Expected the client to safely catch the 503 and return unauthorized");
  }

  @Test
  void processTransaction_when400BadRequest_shouldReturnDeclinedResponse() {
    BankPaymentRequest request = new BankPaymentRequest();

    when(restTemplate.postForEntity(eq(mockBankUrl), any(HttpEntity.class), eq(BankPaymentResponse.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    BankPaymentResponse actualResponse = bankSimulatorClient.processTransaction(request);

    assertFalse(actualResponse.isAuthorized(), "Expected the client to safely catch the 400 and return unauthorized");
  }

  @Test
  void processTransaction_whenGenericExceptionOccurs_shouldReturnDeclinedResponse() {
    BankPaymentRequest request = new BankPaymentRequest();

    when(restTemplate.postForEntity(eq(mockBankUrl), any(HttpEntity.class), eq(BankPaymentResponse.class)))
        .thenThrow(new RuntimeException("Connection Refused"));

    BankPaymentResponse actualResponse = bankSimulatorClient.processTransaction(request);

    assertFalse(actualResponse.isAuthorized(), "Expected the client to safely catch generic exceptions and return unauthorized");
  }
}
