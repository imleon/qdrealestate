package com.lostleon.qdrealestate.crawler;

public class CrawlException extends Exception {

	private static final long serialVersionUID = -4887521316463953292L;

	public CrawlException(String message) {
		super(message);
	}

	public CrawlException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
