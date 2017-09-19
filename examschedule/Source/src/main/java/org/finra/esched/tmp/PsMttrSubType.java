package org.finra.esched.tmp;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "psComponent")
@XmlAccessorType(XmlAccessType.NONE) 
@Entity
@DiscriminatorValue("N")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Cacheable
public class PsMttrSubType extends PsMttrTp{
	
	private final Logger log = LoggerFactory.getLogger(PsMttrSubType.class);
	
	
}
