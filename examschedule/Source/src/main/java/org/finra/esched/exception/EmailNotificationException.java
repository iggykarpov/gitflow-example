package org.finra.esched.exception;

public class EmailNotificationException extends Exception {
	public EmailNotificationException(String message) {
		super(message);
	}

	public EmailNotificationException(Throwable cause) {
		super(cause);
	}
}
