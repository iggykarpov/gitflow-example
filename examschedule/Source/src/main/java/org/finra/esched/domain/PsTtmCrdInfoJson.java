package org.finra.esched.domain;

public class PsTtmCrdInfoJson extends PsTtmStatusJson {
	
    String crdId;
    String typeDs;
    String branchCrdId;
    
    public String getCrdId() {
          return crdId;
    }
    public void setCrdId(String crdId) {
          this.crdId = crdId;
    }
    public String getTypeDs() {
          return typeDs;
    }
    public void setTypeDs(String typeDs) {
          this.typeDs = typeDs;
    }
    public String getBranchCrdId() {
          return branchCrdId;
    }
    public void setBranchCrdId(String branchCrdId) {
          this.branchCrdId = branchCrdId;
    }
}    

