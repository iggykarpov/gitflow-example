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
@Table(name="schdl_mkt_firm_bllbl_entty_vw")
public class PsFirmMarketBillableEntity {
	
	@Id
	@GeneratedValue
	@Column(name="schdl_mkt_firm_bllbl_entty_id")
	private int firmBillableEntityId;
	@Column(name="FIRM_ID")
	private int firmId;
	@Column(name="MKT_TYPE_CD")
	private String marketTypeCode;	
	@Column(name="BLLBL_ENTTY_ID")
	private int billableEntityId;


	public PsFirmMarketBillableEntity() {

	}

	public int getFirmBillableEntityId() {
		return firmBillableEntityId;
	}


	public void setFirmBillableEntityId(int firmBillableEntityId) {
		this.firmBillableEntityId = firmBillableEntityId;
	}
	
	public int getFirmId() {
		return firmId;
	}


	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}


	public String getMarketTypeCode() {
		return marketTypeCode;
	}


	public void setMarketTypeCode(String marketTypeCode) {
		this.marketTypeCode = marketTypeCode;
	}


	public int getBillableEntityId() {
		return billableEntityId;
	}


	public void setBillableEntityId(int billableEntityId) {
		this.billableEntityId = billableEntityId;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsFirmMarketBillableEntity) {
			PsFirmMarketBillableEntity o = (PsFirmMarketBillableEntity)obj;
            return firmBillableEntityId==o.firmBillableEntityId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(firmBillableEntityId);
	}
	
}
