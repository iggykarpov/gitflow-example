package org.finra.esched.exception;

/**
 * @author atsirel
 * @since 9/3/2015
 */
public class ExamOrMatterCreationException extends Exception {
    public ExamOrMatterCreationException(String message) {
        super(message);
    }

    public ExamOrMatterCreationException(Throwable cause) {
        super(cause);
    }
}
