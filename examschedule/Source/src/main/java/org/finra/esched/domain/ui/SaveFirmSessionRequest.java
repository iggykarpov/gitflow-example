package org.finra.esched.domain.ui;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by puppalaa on 7/11/2017.
 */
public class SaveFirmSessionRequest {


    private Long id;

    private Boolean spApprovedFl;
    private Boolean fnApprovedFl;

    private String fwsdDt;
    private String ewsdDt;

    List<SaveComponentRequest> componentRequestList;

    private String flDistrictCd;

    private Boolean flApprovedFl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isSpApprovedFl() {
        return spApprovedFl;
    }

    public void setSpApprovedFl(Boolean spApprovedFl) {
        this.spApprovedFl = spApprovedFl;
    }

    public Boolean isFnApprovedFl() {
        return fnApprovedFl;
    }

    public void setFnApprovedFl(Boolean fnApprovedFl) {
        this.fnApprovedFl = fnApprovedFl;
    }

    public Boolean getSpApprovedFl() {
        return spApprovedFl;
    }

    public Boolean getFnApprovedFl() {
        return fnApprovedFl;
    }

    public String getFwsdDt() {
        return fwsdDt;
    }

    public void setFwsdDt(String fwsdDt) {
        this.fwsdDt = fwsdDt;
    }

    public String getEwsdDt() {
        return ewsdDt;
    }

    public void setEwsdDt(String ewsdDt) {
        this.ewsdDt = ewsdDt;
    }

    public List<SaveComponentRequest> getComponentRequestList() {
        return componentRequestList;
    }

    public void setComponentRequestList(List<SaveComponentRequest> componentRequestList) {
        this.componentRequestList = componentRequestList;
    }

    public String getFlDistrictCd() {
        return flDistrictCd;
    }

    public void setFlDistrictCd(String flDistrictCd) {
        this.flDistrictCd = flDistrictCd;
    }

    public Boolean getFlApprovedFl() {
        return flApprovedFl;
    }

    public void setFlApprovedFl(Boolean flApprovedFl) {
        this.flApprovedFl = flApprovedFl;
    }
}


