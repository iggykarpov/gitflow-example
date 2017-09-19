package org.finra.esched.domain;


public class PsMatterContact implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsMatterContact() {
	}
	
	private Integer firmId;
	private Integer branchId;
	private Integer repId;
	private Integer contactType;
	private Boolean primaryFlag;
	private Boolean potentialRespondentFlag;

	public Integer getFirmId() {
		return firmId;
	}

	public void setFirmId(Integer firmId) {
		this.firmId = firmId;
	}

	public Integer getBranchId() {
		return branchId;
	}

	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
	}

	public Integer getRepId() {
		return repId;
	}

	public void setRepId(Integer repId) {
		this.repId = repId;
	}

	public Integer getContactType() {
		return contactType;
	}

	public void setContactType(Integer contactType) {
		this.contactType = contactType;
	}

	public Boolean getPrimaryFlag() {
		return primaryFlag;
	}

	public void setPrimaryFlag(Boolean primaryFlag) {
		this.primaryFlag = primaryFlag;
	}

	public Boolean getPotentialRespondentFlag() {
		return potentialRespondentFlag;
	}

	public void setPotentialRespondentFlag(Boolean potentialRespondentFlag) {
		this.potentialRespondentFlag = potentialRespondentFlag;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsMatterContact) {
			PsMatterContact o = (PsMatterContact)obj;
            return Integer.valueOf(firmId + "" + branchId + "") == Integer.valueOf(o.firmId + "" + o.branchId + "");
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int)( 31 * Integer.valueOf(firmId + "" + branchId + ""));
	}
}
