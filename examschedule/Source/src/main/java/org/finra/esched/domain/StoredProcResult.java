package org.finra.esched.domain;

import javax.persistence.*;


public class StoredProcResult implements java.io.Serializable {

   
    private String status;
    private String message;
    
	public String getStatus() {
        return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

    
}