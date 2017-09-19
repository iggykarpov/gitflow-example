package org.finra.esched.domain.ui;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsDistrictView implements java.io.Serializable {
	
	@Id
	@Column(name = "ID")
	private Long id;
	@Column(name = "CODE")
	private String code;
	@Column(name = "NAME")
	private String name;
	
	public PsDistrictView() {
	}
	
	public PsDistrictView(Long id, String code, String name){
		this.id=id;
		this.code=code;
		this.name=name;
	}

	@JsonProperty("id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
