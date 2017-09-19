package org.finra.esched.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@XmlRootElement(name = "psMatterStaff")
@XmlAccessorType(XmlAccessType.NONE) 
@Immutable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsMatterStaff implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public PsMatterStaff() {
	}
	@Id
	@Column(name = "ID")
	private Long id;
	@Column(name = "aplcn_user_id")
	private Integer staffId;
	@Column(name = "user_nm")
	private String userId;	
	@Column(name = "role_id")
	private Integer roleId;
	@Column(name = "prmry_fl")
	private String primaryFlag;
	
	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getPrimaryFlag() {
		return primaryFlag;
	}

	public void setPrimaryFlag(String primaryFlag) {
		this.primaryFlag = primaryFlag;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsMatterStaff) {
			PsMatterStaff o = (PsMatterStaff)obj;
            return Integer.valueOf(staffId + "" + roleId + "") == Integer.valueOf(o.staffId + "" + o.roleId + "");
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int)( 31 * Integer.valueOf(staffId + "" + roleId + ""));
	}
}
