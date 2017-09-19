package org.finra.esched.domain;

import java.util.List;


public class PsTtmStatusJson {
	
    //code: -1, -2, -3 and 1
    //codeDS: exiting, updated, deleted and success
    int code;
    String codeDS;
    List<PsTtmErrorJson> errors;
    
    public int getCode() {
          return code;
    }
    public void setCode(int code) {
          this.code = code;
    }
    public String getCodeDS() {
          return codeDS;
    }
    public void setCodeDS(String codeDS) {
          this.codeDS = codeDS;
    }
    
    public List<PsTtmErrorJson> getErrors() {
          return errors;
    }
    public void setErrors(List<PsTtmErrorJson> errors) {
          this.errors = errors;
    }
} 

