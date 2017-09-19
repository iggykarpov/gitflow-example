package org.finra.esched.exception;

/**
 * Created by puppalaa on 7/12/2017.
 */
public class PsSessionStatusException extends Exception  {

    public PsSessionStatusException(String message) {
        super(message);
    }

    public PsSessionStatusException(Throwable cause) {
        super(cause);
    }

}
