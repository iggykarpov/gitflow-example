package org.finra.esched.domain;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class PsTtmContact implements Serializable {
	@JsonProperty("name")
    String name;
	@JsonProperty("type")
    String type;
	@JsonProperty("contactMapId")
    String contactMapId;
	@JsonProperty("primaryFl")
    String primaryFl;
	@JsonProperty("respondentFl")
    String respondentFl;
	@JsonProperty("crdInfo")
    PsTtmCrdInfoJson crdInfo;
	@JsonProperty("timeStamp")
    String timeStamp;
	@JsonProperty("contactTypeList")
    List <Object> contactTypeList;
	@JsonProperty("potRespondentFl")
    String potRespondentFl;
	@JsonProperty("xtrnlAppNm")
    String xtrnlAppNm;
	@JsonProperty("xtrnlCntMapId")
    String xtrnlCntMapId;	
	

    public String getRespondentFl() {
          return respondentFl;
    }

    public void setRespondentFl(String respondentFl) {
          this.respondentFl = respondentFl;
    }

    public List<Object> getContactTypeList() {
          return contactTypeList;
    }

    public void setContactTypeList(List<Object> contactTypeList) {
          this.contactTypeList = contactTypeList;
    }
    
    public String getTimeStamp() {
          return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
          this.timeStamp = timeStamp;
    }

    public PsTtmCrdInfoJson getCrdInfo() {
          return crdInfo;
    }

    public void setCrdInfo(PsTtmCrdInfoJson crdInfo) {
          this.crdInfo = crdInfo;
    }

    public String getPrimaryFl() {
          return primaryFl;
    }

    public void setPrimaryFl(String primaryFl) {
          this.primaryFl = primaryFl;
    }

    public String getContactMapId() {
          return contactMapId;
    }

    public void setContactMapId(String contactMapId) {
          this.contactMapId = contactMapId;
    }

    public String getName() {
          return name;
    }

    public void setName(String name) {
          this.name = name;
    }

    public String getType() {
          return type;
    }

    public void setType(String type) {
          this.type = type;
    }

	public String getPotRespondentFl() {
		return potRespondentFl;
	}

	public void setPotRespondentFl(String potRespondentFl) {
		this.potRespondentFl = potRespondentFl;
	}

	public String getXtrnlAppNm() {
		return xtrnlAppNm;
	}

	public void setXtrnlAppNm(String xtrnlAppNm) {
		this.xtrnlAppNm = xtrnlAppNm;
	}

	public String getXtrnlCntMapId() {
		return xtrnlCntMapId;
	}

	public void setXtrnlCntMapId(String xtrnlCntMapId) {
		this.xtrnlCntMapId = xtrnlCntMapId;
	}

} 