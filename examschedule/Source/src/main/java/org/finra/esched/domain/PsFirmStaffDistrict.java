package org.finra.esched.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import org.hibernate.annotations.Immutable;

@XmlRootElement(name = "psFirmStaffDistrict")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_firm_staff_dstrt_vw")
public class PsFirmStaffDistrict {
	
	private int firmId;
	private Long mainBranchId;
	private Integer spCoordinator;
	private Integer finopCoordinator;
	private Integer spSupervisor;
	private Integer finopSupervisor;	
	private String spSupervisorId;
	private String finopSupervisorId;	
	private String spSupervisorName;
	private String finopSupervisorName;
	private String spSupervisorEmail;
	private String finopSupervisorEmail;	
	private String spCoordinatorId;
	private String finopCoordinatorId;	
	private String spCoordinatorName;
	private String finopCoordinatorName;
	private String spCoordinatorEmail;
	private String finopCoordinatorEmail;		
	
	public PsFirmStaffDistrict() {

	}

	@Id
	@GeneratedValue
	@Column(name="FIRM_CRD_NB")
	public int getFirmId() {
		return firmId;
	}
	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}


	@Column(name="MAIN_BRNCH_ID")	
	public Long getMainBranchId() {
		return mainBranchId;
	}

	public void setMainBranchId(Long mainBranchId) {
		this.mainBranchId = mainBranchId;
	}
	@Column(name="SP_RGLTY_CDTR_STAFF_ID")
	public Integer getSpCoordinator() {
		return spCoordinator;
	}

	public void setSpCoordinator(Integer spCoordinator) {
		this.spCoordinator = spCoordinator;
	}
	@Column(name="RGLTY_CDTR_STAFF_ID")
	public Integer getFinopCoordinator() {
		return finopCoordinator;
	}

	public void setFinopCoordinator(Integer finopCoordinator) {
		this.finopCoordinator = finopCoordinator;
	}
	@Column(name="SP_SPRV_STAFF_ID")
	public Integer getSpSupervisor() {
		return spSupervisor;
	}

	public void setSpSupervisor(Integer spSupervisor) {
		this.spSupervisor = spSupervisor;
	}
	@Column(name="FINOP_SPRV_STAFF_ID")
	public Integer getFinopSupervisor() {
		return finopSupervisor;
	}

	public void setFinopSupervisor(Integer finopSupervisor) {
		this.finopSupervisor = finopSupervisor;
	}
	@Column(name="SP_SPRV_USER_ID")
	public String getSpSupervisorId() {
		return spSupervisorId;
	}

	public void setSpSupervisorId(String spSupervisorId) {
		this.spSupervisorId = spSupervisorId;
	}
	@Column(name="FINOP_SPRV_USER_ID")
	public String getFinopSupervisorId() {
		return finopSupervisorId;
	}

	public void setFinopSupervisorId(String finopSupervisorId) {
		this.finopSupervisorId = finopSupervisorId;
	}
	@Column(name="SP_SPRV_USER_FULL_NM")
	public String getSpSupervisorName() {
		return spSupervisorName;
	}

	public void setSpSupervisorName(String spSupervisorName) {
		this.spSupervisorName = spSupervisorName;
	}
	@Column(name="FINOP_SPRV_USER_FULL_NM")
	public String getFinopSupervisorName() {
		return finopSupervisorName;
	}

	public void setFinopSupervisorName(String finopSupervisorName) {
		this.finopSupervisorName = finopSupervisorName;
	}
	@Column(name="SP_SPRV_EMAIL_ADRS_TX")
	public String getSpSupervisorEmail() {
		return spSupervisorEmail;
	}

	public void setSpSupervisorEmail(String spSupervisorEmail) {
		this.spSupervisorEmail = spSupervisorEmail;
	}
	@Column(name="FINOP_SPRV_EMAIL_ADRS_TX")
	public String getFinopSupervisorEmail() {
		return finopSupervisorEmail;
	}

	public void setFinopSupervisorEmail(String finopSupervisorEmail) {
		this.finopSupervisorEmail = finopSupervisorEmail;
	}
	@Column(name="SP_CDTR_USER_ID")
	public String getSpCoordinatorId() {
		return spCoordinatorId;
	}

	public void setSpCoordinatorId(String spCoordinatorId) {
		this.spCoordinatorId = spCoordinatorId;
	}
	@Column(name="FINOP_CDTR_USER_ID")
	public String getFinopCoordinatorId() {
		return finopCoordinatorId;
	}

	public void setFinopCoordinatorId(String finopCoordinatorId) {
		this.finopCoordinatorId = finopCoordinatorId;
	}
	@Column(name="SP_CDTR_USER_FULL_NM")
	public String getSpCoordinatorName() {
		return spCoordinatorName;
	}

	public void setSpCoordinatorName(String spCoordinatorName) {
		this.spCoordinatorName = spCoordinatorName;
	}
	@Column(name="FINOP_CDTR_USER_FULL_NM")
	public String getFinopCoordinatorName() {
		return finopCoordinatorName;
	}

	public void setFinopCoordinatorName(String finopCoordinatorName) {
		this.finopCoordinatorName = finopCoordinatorName;
	}
	@Column(name="SP_CDTR_EMAIL_ADRS_TX")
	public String getSpCoordinatorEmail() {
		return spCoordinatorEmail;
	}

	public void setSpCoordinatorEmail(String spCoordinatorEmail) {
		this.spCoordinatorEmail = spCoordinatorEmail;
	}
	@Column(name="FINOP_CDTR_EMAIL_ADRS_TX")
	public String getFinopCoordinatorEmail() {
		return finopCoordinatorEmail;
	}

	public void setFinopCoordinatorEmail(String finopCoordinatorEmail) {
		this.finopCoordinatorEmail = finopCoordinatorEmail;
	}	

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsFirmStaffDistrict) {
			PsFirmStaffDistrict o = (PsFirmStaffDistrict)obj;
            return firmId==o.firmId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(firmId);
	}
	
}
