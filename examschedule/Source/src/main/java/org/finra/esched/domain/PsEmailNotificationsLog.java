package org.finra.esched.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement(name = "psEmailNotificationsLog")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_exam_wrksp_email_log")
public class PsEmailNotificationsLog {

	private int logId;
	private Integer firmId;
	private String typeCd;
	private String sttsCd;
	private Date sentDate;
	private String request;
	private String response;
	
	public PsEmailNotificationsLog() {

	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psEmailNotificationsLogSeqSequence")
	@SequenceGenerator(name = "psEmailNotificationsLogSeqSequence", sequenceName = "schdl_ew_email_log_id_seq", allocationSize=1)
	@Column(name="schdl_exam_wrksp_email_log_id")	
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	
	@Column(name="firm_id")	
	public Integer getFirmId() {
		return firmId;
	}
	public void setFirmId(Integer firmId) {
		this.firmId = firmId;
	}

	@Column(name="email_type_cd")	
	public String getTypeCd() {
		return typeCd;
	}
	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}

	@Column(name="email_stts_cd")	
	public String getSttsCd() {
		return sttsCd;
	}
	public void setSttsCd(String sttsCd) {
		this.sttsCd = sttsCd;
	}

	@Column(name="email_sent_dt")	
	public Date getSentDate() {
		return sentDate;
	}
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@Column(name="rqst_tx")
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	
	@Column(name="rspns_tx")
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsEmailNotificationsLog) {
			PsEmailNotificationsLog o = (PsEmailNotificationsLog)obj;
			return Objects.equals(logId, o.logId);
		} else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return  31 *(logId);
	}
	
}
