package org.finra.esched.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement(name = "starSyncLog")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="SCHDL_STAR_SYNC_LOG")
public class PsStarSyncLog {

	private Long logId;
	private Long outputId;
	private String publishType;
	private String status;
	private Date publishDate;
	private String request;
	private String response;
	private Long userId;
	
	public PsStarSyncLog() {

	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psStarSyncLogSeqSequence")
	@SequenceGenerator(name = "psStarSyncLogSeqSequence", sequenceName = "schdl_star_sync_log_id_seq", allocationSize=1)
	@Column(name="schdl_star_sync_log_id")	
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
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
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
		
		if (obj instanceof PsStarSyncLog) {
			PsStarSyncLog o = (PsStarSyncLog)obj;
			return Objects.equals(logId, o.logId);
		} else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return  31 *(logId.intValue());
	}
	
}
