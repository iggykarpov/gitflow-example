package org.finra.esched.domain.ui;

/**
 * Created by puppalaa on 7/12/2017.
 */
public class SaveComponentRequest {

    private Integer id;
    private Boolean reqrdOvrrdFl;
    private Integer ovrdReasonId;
    private String businessReviewText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getReqrdOvrrdFl() {
        return reqrdOvrrdFl;
    }

    public void setReqrdOvrrdFl(Boolean reqrdOvrrdFl) {
        this.reqrdOvrrdFl = reqrdOvrrdFl;
    }

    public Integer getOvrdReasonId() {
        return ovrdReasonId;
    }

    public void setOvrdReasonId(Integer ovrdReasonId) {
        this.ovrdReasonId = ovrdReasonId;
    }

    public String getBusinessReviewText() {
        return businessReviewText;
    }

    public void setBusinessReviewText(String businessReviewText) {
        this.businessReviewText = businessReviewText;
    }
}
