package org.finra.esched.exception;

//TODO This should be removed - no longer used in Schedule
/**
 * @author atsirel
 * @since 9/3/2015
 */
public class ExamOrMatterLinkingException extends Exception {
    public ExamOrMatterLinkingException(String message) {
        super(message);
    }

    public ExamOrMatterLinkingException(Throwable cause) {
        super(cause);
    }
}
