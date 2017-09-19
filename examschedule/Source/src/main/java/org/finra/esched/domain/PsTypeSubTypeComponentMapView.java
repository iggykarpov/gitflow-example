package org.finra.esched.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "schdl_type_sub_type_cmpnt_vw")
@Immutable

public class PsTypeSubTypeComponentMapView  implements java.io.Serializable {


    private Integer typeSubTypeComponentMapId;
    private String typeSubTypeCode;
    private String examSubTypeCode;
    private String examTypeSubTypeCode;
    private String examTypeSubTypeDescription;
    private String matterTypeSubTypeId;
    private String activeFlag;
    private String finopFlag;
    private String firstFinopFlag;
    private String spFlag;
    private String optionFlag;
    private String municipalFlag;
    private String municipalAdvisorFlag;
    private String rsaFinopFlag;
    private String rsaSpFlag;
    private String floorFlag;
    private String ancFlag;
    private String sdfFlag;
    private String moreComponentsFlag;
    private String lastUpdateUserId;
    private Date lastUpdateDate;
    private String lastUpdateUserName;

    public PsTypeSubTypeComponentMapView() {

    }
/*
    @JsonProperty("examSubTypeCode")
    @Column(name = "exam_sub_type_cd")
    public String getExamSubTypeCode() {
        return examSubTypeCode;
    }
    public void setExamSubTypeCode(String examSubTypeCode) {
        this.examSubTypeCode = examSubTypeCode;
    }
*/

    @Column(name = "type_sub_type_cmpnt_map_id")
    @JsonProperty("typeSubTypeComponentMapId")
    public Integer getTypeSubTypeComponentMapId() {
        return typeSubTypeComponentMapId;
    }
    public void setTypeSubTypeComponentMapId(Integer typeSubTypeComponentMapId) {
        this.typeSubTypeComponentMapId = typeSubTypeComponentMapId;
    }

    @JsonProperty("typeSubTypeCode")
    @Column(name = "type_sub_type_cd")
    public String getTypeSubTypeCode() {
        return typeSubTypeCode;
    }
    public void setTypeSubTypeCode(String typeSubTypeCode) {
        this.typeSubTypeCode = typeSubTypeCode;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psTypeSubTypeComponentSequence")
    @SequenceGenerator(name = "psTypeSubTypeComponentSequence", sequenceName = "schdl_typ_sub_typ_cmpnt_id_seq", allocationSize = 1)
    @JsonProperty("examTypeSubTypeCode")
    @Column(name = "exam_type_sub_type_cd")
    public String getExamTypeSubTypeCode() {
        return examTypeSubTypeCode;
    }
    public void setExamTypeSubTypeCode(String examTypeSubTypeCode) {
        this.examTypeSubTypeCode = examTypeSubTypeCode;
    }

    @JsonProperty("examTypeSubTypeDescription")
    @Column(name = "exam_type_sub_type_ds")
    public String getExamTypeSubTypeDescription() {
        return examTypeSubTypeDescription;
    }
    public void setExamTypeSubTypeDescription(String examTypeSubTypeDescription) {
        this.examTypeSubTypeDescription = examTypeSubTypeDescription;
    }

    @JsonProperty("matterTypeSubTypeId")
    @Column(name = "mttr_type_sub_type_id")
    public String getMatterTypeSubTypeId() {
        return matterTypeSubTypeId;
    }
    public void setMatterTypeSubTypeId(String matterTypeSubTypeId) {
        this.matterTypeSubTypeId = matterTypeSubTypeId;
    }

    @JsonProperty("activeFlag")
    @Column(name = "actv_fl")
    public String getActiveFlag() {
        return activeFlag;
    }
    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    @JsonProperty("finopFlag")
    @Column(name = "finop_fl")
    public String getFinopFlag() {
        return finopFlag;
    }
    public void setFinopFlag(String finopFlag) {
        this.finopFlag = finopFlag;
    }

    @JsonProperty("firstFinopFlag")
    @Column(name = "first_finop_fl")
    public String getFirstFinopFlag() {
        return firstFinopFlag;
    }
    public void setFirstFinopFlag(String firstFinopFlag) {
        this.firstFinopFlag = firstFinopFlag;
    }

    @JsonProperty("spFlag")
    @Column(name = "sp_fl")
    public String getSpFlag() {
        return spFlag;
    }
    public void setSpFlag(String spFlag) {
        this.spFlag = spFlag;
    }

    @JsonProperty("optionFlag")
    @Column(name = "option_fl")
    public String getOptionFlag() {
        return optionFlag;
    }
    public void setOptionFlag(String optionFlag) {
        this.optionFlag = optionFlag;
    }

    @JsonProperty("municipalFlag")
    @Column(name = "mncpl_fl")
    public String getMunicipalFlag() {
        return municipalFlag;
    }
    public void setMunicipalFlag(String municipalFlag) {
        this.municipalFlag = municipalFlag;
    }

    @JsonProperty("municipalAdvisorFlag")
    @Column(name = "mncpl_advsr_fl")
    public String getMunicipalAdvisorFlag() {
        return municipalAdvisorFlag;
    }
    public void setMunicipalAdvisorFlag(String municipalAdvisorFlag) {
        this.municipalAdvisorFlag = municipalAdvisorFlag;
    }

    @JsonProperty("rsaFinopFlag")
    @Column(name = "rsa_finop_fl")
    public String getRsaFinopFlag() {
        return rsaFinopFlag;
    }
    public void setRsaFinopFlag(String rsaFinopFlag) {
        this.rsaFinopFlag = rsaFinopFlag;
    }

    @JsonProperty("rsaSpFlag")
    @Column(name = "rsa_sp_fl")
    public String getRsaSpFlag() {
        return rsaSpFlag;
    }
    public void setRsaSpFlag(String rsaSpFlag) {
        this.rsaSpFlag = rsaSpFlag;
    }

    @JsonProperty("floorFlag")
    @Column(name = "floor_fl")
    public String getFloorFlag() {
        return floorFlag;
    }
    public void setFloorFlag(String floorFlag) {
        this.floorFlag = floorFlag;
    }

    @JsonProperty("ancFlag")
    @Column(name = "anc_fl")
    public String getAncFlag() {
        return ancFlag;
    }
    public void setAncFlag(String ancFlag) {
        this.ancFlag = ancFlag;
    }

    @JsonProperty("sdfFlag")
    @Column(name = "sdf_fl")
    public String getSdfFlag() {
        return sdfFlag;
    }
    public void setSdfFlag(String sdfFlag) {
        this.sdfFlag = sdfFlag;
    }

    @JsonProperty("moreComponentsFlag")
    @Column(name = "more_cmpnt_fl")
    public String getMoreComponentsFlag() {
        return moreComponentsFlag;
    }
    public void setMoreComponentsFlag(String moreComponentsFlag) {
        this.moreComponentsFlag = moreComponentsFlag;
    }

    @JsonProperty("lastUpdateUserId")
    @Column(name = "last_updt_user_id")
    public String getLastUpdateUserId() {
        return lastUpdateUserId;
    }
    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    @JsonProperty("lastUpdateDate")
    @Column(name = "last_updt_dt")
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @JsonProperty("userFullName")
    @Column(name = "user_full_nm")
    public String getLastUpdateUserName() {
        return lastUpdateUserName;
    }
    public void setLastUpdateUserName(String lastUpdateUserName) {
        this.lastUpdateUserName = lastUpdateUserName;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        if (obj instanceof PsTypeSubTypeComponentMapView) {
            PsTypeSubTypeComponentMapView o = (PsTypeSubTypeComponentMapView) obj;
            return typeSubTypeComponentMapId == o.typeSubTypeComponentMapId ;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) 31 * (typeSubTypeComponentMapId);
    }


}
