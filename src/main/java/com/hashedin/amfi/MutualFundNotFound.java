package com.hashedin.amfi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Mutual fund not found")
public class MutualFundNotFound extends RuntimeException {

	private static final long serialVersionUID = 513737017831616865L;
	private final String id;
	
	public MutualFundNotFound(String id) {
		super("Cannot fund mutual fund with id = " + id);
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
