package org.finra.esched.domain;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.finra.esched.domain.PsApplicableCmp.RESP_DISTR_TYPE;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)*/
@XmlRootElement(name = "psLastExamProjected")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@Immutable
@Table(name="schdl_last_exams_prjtd_vw")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsLastExamProjected implements java.io.Serializable{
	

	private PsLastExamProjectedPK id;
	private Date projectedDate;
	private PsLastExam lastExam;
	
	public PsLastExamProjected() {
	}	
	
	@EmbeddedId
	public PsLastExamProjectedPK getId() {
		return id;
	}
	public void setId(PsLastExamProjectedPK id) {
		this.id = id;
	}
	@Column(name="EVENT_DATE")
	public Date getProjectedDate() {
		return projectedDate;
	}
	public void setProjectedDate(Date projectedDate) {
		this.projectedDate = projectedDate;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="FIRM_ID", insertable=false, updatable=false),
		@JoinColumn(name="SCHDL_EXAM_SNPSH_ID", insertable=false, updatable=false),
		@JoinColumn(name="SCHDL_CMPNT_CD", insertable=false, updatable=false)
	})
	@Cascade(CascadeType.PERSIST)
	@XmlElement(name = "lastexam", required = true)
	public PsLastExam getLastExam() {
		return lastExam;
	}
	public void setLastExam(PsLastExam lastExam) {
		this.lastExam = lastExam;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (obj instanceof PsLastExamProjected) {
			PsLastExamProjected o = (PsLastExamProjected)obj;
            return id == o.id;
        } else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		return (int) 31 *(id.hashCode());
	}

}
