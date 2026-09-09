# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

## Documentation 
### Bank Simulator Client 
* Executes HTTP POST request to the external (Mountebank) API
* Handles timeout, 4xx and 5xx errors
* DTO Models: BankPaymentRequest and BankPaymentResponse 

The BankSimulatorClient and associated Data Transfer Objects (DTOs) manage all external HTTP communication between the gateway and the acquiring bank.

### Payment Gateway Service
processPayment workflow:
* pre-authorization validation
* data transformation 
* external execution
* status resolution
* data sanitization 
* state persistence

### Payment Gateway Controller
New endpoint: POST /payment 
* Input validation: Uses Jakarta @Valid annotation to enforce payload check at the API boundary
* PostPaymentResponse: returns masked card number and omitted CVV 

## Assumptions Made
* In-Memory Persistence uses HashMap and is left as is
* Idempotency is Out of Scope
* Authentication is Out of Scope