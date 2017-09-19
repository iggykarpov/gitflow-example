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

@XmlRootElement(name = "psFirmStaffMarket")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_mkt_mttr_data_vw")
public class PsFirmStaffMarket {
	
	@Id
	@GeneratedValue
	@Column(name="FIRM_CRD_NB")
	private int firmId;
	@Column(name="MAIN_BRNCH_ID")	
	private Long mainBranchId;
	@Column(name="MNGR_STAFF_ID")
	private Integer managerStaffId;
	@Column(name="MNGR_USER_ID")
	private String managerUserId;
	@Column(name="MKT_TYPE_CD")
	private String marketTypeCode;	
	@Column(name="DSTRT_ID")
	private Integer districtId;
	@Column(name="DSTRT_CD")
	private String districtCode;
	@Column(name="EXAM_TYPE_CD")
	private String examTypeCode;
	@Column(name="EXAM_SUB_TYPE_CD")
	private String examSubTypeCode;
	@Column(name="MTTR_TYPE_ID")
	private Integer matterTypeId;
	@Column(name="MTTR_SUB_TYPE_ID")
	private Integer matterSubTypeId;
	@Column(name="PRDCT_ID")
	private Integer productId;
	@Column(name="MTTR_DESC_TX")
	private String matterDescription;
	@Column(name="RGLTY_SGNFC_ID")
	private String regulatorSignificanceId;
	@Column(name="MTTR_CMMNT_STAFF_ID")
	private Integer matterCommentStaffId;
	
	
	public PsFirmStaffMarket() {

	}


	public int getFirmId() {
		return firmId;
	}



	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}



	public Long getMainBranchId() {
		return mainBranchId;
	}



	public void setMainBranchId(Long mainBranchId) {
		this.mainBranchId = mainBranchId;
	}



	public Integer getManagerStaffId() {
		return managerStaffId;
	}



	public void setManagerStaffId(Integer managerStaffId) {
		this.managerStaffId = managerStaffId;
	}



	public String getManagerUserId() {
		return managerUserId;
	}



	public void setManagerUserId(String managerUserId) {
		this.managerUserId = managerUserId;
	}



	public String getMarketTypeCode() {
		return marketTypeCode;
	}


	public void setMarketTypeCode(String marketTypeCode) {
		this.marketTypeCode = marketTypeCode;
	}


	public Integer getDistrictId() {
		return districtId;
	}


	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}


	public String getDistrictCode() {
		return districtCode;
	}


	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}


	public String getExamTypeCode() {
		return examTypeCode;
	}


	public void setExamTypeCode(String examTypeCode) {
		this.examTypeCode = examTypeCode;
	}


	public String getExamSubTypeCode() {
		return examSubTypeCode;
	}


	public void setExamSubTypeCode(String examSubTypeCode) {
		this.examSubTypeCode = examSubTypeCode;
	}


	public Integer getMatterTypeId() {
		return matterTypeId;
	}


	public void setMatterTypeId(Integer matterTypeId) {
		this.matterTypeId = matterTypeId;
	}


	public Integer getMatterSubTypeId() {
		return matterSubTypeId;
	}


	public void setMatterSubTypeId(Integer matterSubTypeId) {
		this.matterSubTypeId = matterSubTypeId;
	}


	public Integer getProductId() {
		return productId;
	}


	public void setProductId(Integer productId) {
		this.productId = productId;
	}


	public String getMatterDescription() {
		return matterDescription;
	}


	public void setMatterDescription(String matterDescription) {
		this.matterDescription = matterDescription;
	}


	public String getRegulatorSignificanceId() {
		return regulatorSignificanceId;
	}


	public void setRegulatorSignificanceId(String regulatorSignificanceId) {
		this.regulatorSignificanceId = regulatorSignificanceId;
	}


	public Integer getMatterCommentStaffId() {
		return matterCommentStaffId;
	}


	public void setMatterCommentStaffId(Integer matterCommentStaffId) {
		this.matterCommentStaffId = matterCommentStaffId;
	}


	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsFirmStaffMarket) {
			PsFirmStaffMarket o = (PsFirmStaffMarket)obj;
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
