package com.reliaquest.api.model;


public class EmployeeNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8128331663186601079L;
	int code;
	String message;
	public EmployeeNotFoundException(String message, int code) {
		super(message);
	}

}
