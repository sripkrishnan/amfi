package com.hashedin.amfi;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Get the NAV for a given mutual fund
 * @author sripkrishnan
 *
 */
@Service
public class NavService {
	
	private static final String AMFI_NAV_URL = "https://www.amfiindia.com/spages/NAVAll.txt?t=";
	private final RestTemplate restTemplate;
	private final String amfiNavUrl;
	
	@Autowired
	public NavService(RestTemplateBuilder builder) {
		this.restTemplate = builder
				.setConnectTimeout(Duration.ofSeconds(3))
				.setReadTimeout(Duration.ofSeconds(10))
				.build();
		this.amfiNavUrl = AMFI_NAV_URL;
	}
		
	/**
	 * Gets the NAV for a mutual fund for a given date
	 * @param id the primary key for the mutual fund
	 * @return NAV multiplied by 10^4
	 */
	public long getLatestNav(String id) {
		return fetchNav(id);
	}
	
	/*
	 * Fetches NAV from AMFI's REST API
	 * 
	 * TODO: Performance Optimization 
	 * This method is downloading a large file and only consuming a very small portion of the file
	 * We should cache the response. Alternatively, we should find another REST API
	 * 
	 */
	private long fetchNav(String id) {
		/*
		 * Raw Response is a very big file
		 * Each line contains a mutual fund and it's details, including its NAV
		 * Each line has tokens that are ; separated
		 * 
		 * Here is how it looks like:
		 * 
		 * Scheme Code;ISIN Div Payout/ ISIN Growth;ISIN Div Reinvestment;Scheme Name;Net Asset Value;Date
		 * 
		 * Open Ended Schemes(Debt Scheme - Banking and PSU Fund)
		 * 
		 * 
		 * Aditya Birla Sun Life Mutual Fund
		 * 119551;INF209KA12Z1;INF209KA13Z9;Aditya Birla Sun Life Banking & PSU Debt Fund  - Direct Plan-Dividend;159.817;15-Feb-2021
		 * 119552;INF209K01YM2;-;Aditya Birla Sun Life Banking & PSU Debt Fund - Direct Plan-Monthly Dividend;114.3157;15-Feb-2021
		 * 119553;INF209K01YO8;-;Aditya Birla Sun Life Banking & PSU Debt Fund - Direct Plan-Quarterly Dividend;112.2331;15-Feb-2021
		 * 
		 */
		String rawResponse = this.restTemplate.getForObject(amfiNavUrl, String.class);
		
		String lines[] = rawResponse.split("\n");
		for (String line : lines) {
			if (line.isBlank()) {
				continue;
			}
			// Search for id till you find semi-colon, otherwise id=12 will also match id=128372
			if (line.startsWith(id + ";")) {
				String tokens[] = line.split(";");
				if (tokens.length != 6) {
					throw new IllegalStateException("AMFI response format has changed, expected exactly 6 tokens. \n" + line);
				}
				String navAsText = tokens[4];
				return toLong(navAsText);
			}
		}
		throw new MutualFundNotFound(id);
	}
	
	private long toLong(String navAsText) {
		/*
		 * Effectively, multiply NAV by 10^4 and return as a long
		 */
		navAsText = navAsText.replaceAll(",", "");
		long nav = ((Double)(Double.parseDouble(navAsText) * 10000)).longValue();
		return nav;
	}

}
