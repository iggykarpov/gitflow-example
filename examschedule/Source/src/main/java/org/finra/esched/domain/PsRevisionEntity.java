package org.finra.esched.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.finra.exam.common.audit.BaseRevisionEntity;
import org.finra.exam.common.audit.BaseRevisionListener;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@RevisionEntity(BaseRevisionListener.class)
@Entity
@Table(name="SCHDL_RVSN")
public class PsRevisionEntity implements BaseRevisionEntity {
	 private int id;
	 private long timestamp;    
	 private String username;

	    @Id
	    @RevisionNumber
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduleRevisionSequence")
		@SequenceGenerator(name = "scheduleRevisionSequence", sequenceName = "SCHDL_RVSN_ID_SEQ", allocationSize=1)
	    @Column(name = "RVSN_ID", nullable = false, updatable = false)
	    public int getId()
	    {
	        return id;
	    }

	    public void setId(int id)
	    {
	        this.id = id;
	    }

	    @RevisionTimestamp
	    @Column(name = "RVSN_NB")
	    public long getTimestamp()
	    {
	        return timestamp;
	    }

	    public void setTimestamp(long timestamp)
	    {
	        this.timestamp = timestamp;
	    }

	    @Column(name = "USER_ID")
	    public String getUsername()
	    {
	        return username;
	    }

	    public void setUsername(String username)
	    {
	        this.username = username;
	    }
	
}