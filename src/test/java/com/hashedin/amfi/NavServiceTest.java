package com.hashedin.amfi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;

@RestClientTest(NavService.class)
class NavServiceTest {

	@Autowired
	private NavService service;
	
	@Autowired
	private MockRestServiceServer amfiServer;
	
	@Test
	void testNav() {
		Resource validAmfiResource = new ClassPathResource("amfi/valid-response.txt");
		this.amfiServer
			.expect(ExpectedCount.manyTimes(), anything())
			.andRespond(withSuccess(validAmfiResource, MediaType.TEXT_PLAIN));
		
		assertEquals(service.getLatestNav("119551"), 1598170);
		assertEquals(service.getLatestNav("119552"), 1143157);
		assertEquals(service.getLatestNav("128952"), 15328272, "Comma in NAV");
		assertEquals(service.getLatestNav("120437"), 10340000, "No decimal point in NAV");
	}
	
	@Test
	void testAmfiServiceReturnsErrors() {
		this.amfiServer.expect(anything()).andRespond(withServerError());
		assertThrows(HttpServerErrorException.class, () -> service.getLatestNav("119551"));
	}
	
	@Test
	void testAmfiReturnsMalformedData() {
		/*
		 * AMFI has changed response format
		 */
		Resource validAmfiResource = new ClassPathResource("amfi/invalid-response.txt");
		this.amfiServer
			.expect(ExpectedCount.manyTimes(), anything())
			.andRespond(withSuccess(validAmfiResource, MediaType.TEXT_PLAIN));
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.getLatestNav("119551"));
		assertTrue(exception.getMessage().contains("AMFI response format has changed"));
	}
}
