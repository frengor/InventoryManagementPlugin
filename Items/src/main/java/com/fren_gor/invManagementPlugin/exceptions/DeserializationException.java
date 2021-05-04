package com.fren_gor.invManagementPlugin.exceptions;

public final class DeserializationException extends RuntimeException {

    private static final long serialVersionUID = 5732914764163243723L;

    public DeserializationException() {
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }

}
