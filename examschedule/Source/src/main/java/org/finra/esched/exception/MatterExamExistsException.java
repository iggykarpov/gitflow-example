package org.finra.esched.exception;

/**
 * Created by puppalaa on 7/25/2017.
 */
public class MatterExamExistsException extends Exception{
    public MatterExamExistsException(String message) {
        super(message);
    }

    public MatterExamExistsException(Throwable cause) {
        super(cause);
    }
}
