## Mutual Fund NAV Service

A demo Spring Boot application that exposes a REST API to fetch the latest NAV for a mutual fund. 

AMFI provides a URL to fetch the latest NAVs for all mutual funds, see [https://www.amfiindia.com/spages/NAVAll.txt](https://www.amfiindia.com/spages/NAVAll.txt). Our service extracts the NAV for the given mutual fund from the above URL.

This code demonstrates the following - 

1. Constructor based dependency injection
1. Calling external REST APIs using RestTemplate class
1. Setting up socket and read timeouts when calling external services
1. Handling edge cases / throwing appropriate exceptions when things break
1. Unit tests that exercise external service calls
1. Using MockRestServiceServer to simulate external REST APIs


## Code Walkthrough

For a detailed code walk-through, please review the following pull requests:

1. [Step 1: Create scaffold using start.spring.io](https://github.com/sripkrishnan/amfi/pull/1)
1. [Step 2: Basic service implementation with unit tests](https://github.com/sripkrishnan/amfi/pull/2)
1. [Step 3: Bug Fix: ID based lookup must be exact match](https://github.com/sripkrishnan/amfi/pull/3)

## Running the code

This is a standard Spring Boot project. The initial scaffold was downloaded from https://start.spring.io/. It uses Maven and Java 8.

To run the project: `./mvnw springb-boot:run`