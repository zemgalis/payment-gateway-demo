package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.bank.BankPaymentRequest;
import com.checkout.payment.gateway.model.bank.BankPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class BankSimulatorClient {

  private static final Logger LOG = LoggerFactory.getLogger(BankSimulatorClient.class);

  private final RestTemplate restTemplate;
  private final String bankSimulatorUrl;

  // We inject the RestTemplate configured in ApplicationConfiguration
  public BankSimulatorClient(RestTemplate restTemplate,
      @Value("${bank.simulator.url:http://localhost:8080/payments}") String bankSimulatorUrl) {
    this.restTemplate = restTemplate;
    this.bankSimulatorUrl = bankSimulatorUrl;
  }

  public BankPaymentResponse processTransaction(BankPaymentRequest request) {
    try {
      LOG.debug("Sending payment request to Bank Simulator for amount: {}", request.getAmount());
      ResponseEntity<BankPaymentResponse> response = restTemplate.postForEntity(
          bankSimulatorUrl,
          new HttpEntity<>(request),
          BankPaymentResponse.class
      );
      return response.getBody();

    } catch (HttpClientErrorException | HttpServerErrorException e) {
      // Catches 400 Bad Request or 503 Service Unavailable from the simulator
      LOG.warn("Bank Simulator returned an error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());

      // Return a safely declined/rejected response rather than crashing the gateway
      BankPaymentResponse declinedResponse = new BankPaymentResponse();
      declinedResponse.setAuthorized(false);
      return declinedResponse;
    } catch (Exception e) {
      LOG.error("Unexpected error communicating with Bank Simulator", e);
      BankPaymentResponse errorResponse = new BankPaymentResponse();
      errorResponse.setAuthorized(false);
      return errorResponse;
    }
  }
}
