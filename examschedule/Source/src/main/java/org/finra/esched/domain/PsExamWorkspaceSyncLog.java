package org.finra.esched.domain;

import java.util.Date;

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




import org.hibernate.annotations.Immutable;

@XmlRootElement(name = "starSyncLog")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_exam_wrksp_sync_log")
public class PsExamWorkspaceSyncLog {

	private Long logId;
	private Long outputId;
	private String publishType;
	private String status;
	private Date publishDate;
	private String request;
	private String response;	
	private Integer userId;
	
	public PsExamWorkspaceSyncLog() {

	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psExamWorkspaceSyncLogSeqSequence")
	@SequenceGenerator(name = "psExamWorkspaceSyncLogSeqSequence", sequenceName = "schdl_ew_sync_log_id_seq", allocationSize=1)
	@Column(name="schdl_exam_wrksp_sync_log_id")	
	public Long getLogId() {
		return logId;
	}
	
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	@Column(name="schdl_otpt_id")	
	public Long getOutputId() {
		return outputId;
	}


	public void setOutputId(Long outputId) {
		this.outputId = outputId;
	}


	@Column(name="pblsh_type_cd")
	public String getPublishType() {
		return publishType;
	}
	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}
	@Column(name="pblsh_stts_cd")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Column(name="pblsh_dt")
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
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
	@Column(name="aplcn_user_id")	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsExamWorkspaceSyncLog) {
			PsExamWorkspaceSyncLog o = (PsExamWorkspaceSyncLog)obj;
            return logId==o.logId;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return  31 *(logId.intValue());
	}
	
}
