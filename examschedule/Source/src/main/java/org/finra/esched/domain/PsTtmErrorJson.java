package org.finra.esched.domain;

public class PsTtmErrorJson {
	
       String errorCode;
       String errorMessage;
       
       public String getErrorCode() {
             return errorCode;
       }
       public void setErrorCode(String errorCode) {
             this.errorCode = errorCode;
       }
       public String getErrorMessage() {
             return errorMessage;
       }
       public void setErrorMessage(String errorMessage) {
             this.errorMessage = errorMessage;
       }
       
}
