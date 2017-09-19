package org.finra.esched.domain;

import org.finra.esched.exception.ValidationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by puppalaa on 7/17/2017.
 */
public class PublishBean {
    private static final Long FIN_OP_DOMAIN_ID = 1L;
    private static final Long SP_DOMAIN_ID = 2L;
    private static final Long TFCE_DOMAIN_ID = 3L;
    private static final Long ANC_DOMAIN_ID = 5L;
    private static final Long FLOOR_DOMAIN_ID = 6L;
    private static final String FIN_OP_DISTRICT_TYPE_CODE = "FN";
    private static final String SP_DISTRICT_TYPE_CODE = "SP";

    private PsOutput fnExam;
    private PsOutput spExam;
    private PsOutput ancExam;
    private PsOutput tfceExam;
    private PsOutput floorExam;
    private PsSession session;
    private boolean linkFnAndSp = false;
    private String marketTypeCode;

    public PublishBean(PsSession session, String tfceExamType, String marketTypeCode) throws ValidationException {

        this.session = session;

        if (null == this.session)
            throw new ValidationException("Session object is empty");

        List<PsOutput> psOutputList = session.getExams();

        // Session must have at least one valid PsOutput record. Otherwise
        // it should not be available for scheduling in first place...
        if (psOutputList == null || psOutputList.isEmpty())
            throw new ValidationException(
                    "Session " + this.session.getId() + " doesn't have any records in PsOutput.");
        this.marketTypeCode = marketTypeCode;

        for (PsOutput bean : psOutputList) {
            if (tfceExamType.equals("NonTFCEEXAM") && (
                    PsOutput.FINOP_TYPE.equalsIgnoreCase(bean.getExamTypeCd())
                    || PsOutput.FIRST_FINOP_TYPE.equalsIgnoreCase(bean.getExamTypeCd())
            )) {
                fnExam = bean;
            }else if(tfceExamType.equals("NonTFCEEXAM") && PsOutput.FLOOR_TYPE.equalsIgnoreCase(bean.getExamTypeCd())) {
                if(session.getFlDistrictCd().equalsIgnoreCase(session.getFirm().getFnDistrictCode()))
                    fnExam = bean;
                else
                    floorExam = bean;
            }else if (tfceExamType.equals("NonTFCEEXAM")
                    && (PsOutput.SALES_PRACTICE_TYPE.equalsIgnoreCase(bean.getExamTypeCd())
                    || PsOutput.OPTIONS_TYPE.equalsIgnoreCase(bean.getExamTypeCd())
                    || PsOutput.MUNICIPAL_TYPE.equalsIgnoreCase(bean.getExamTypeCd()))
            ) {
                spExam = bean;
            } else if (tfceExamType.equals("TFCEEXAM") && marketTypeCode.equalsIgnoreCase(bean.getMemberMarketCode())) {
                tfceExam = bean;
            } else if (tfceExamType.equals("NonTFCEEXAM") && bean.getMemberMarketCode() == null) {
                ancExam = bean;
            }
        }

        linkFnAndSp = (null != fnExam) && (spExam != null);
    }

    public boolean hasFnMatter() {
        return null != fnExam && fnExam.hasMatter();
    }

    public boolean hasSpMatter() {
        return null != spExam && spExam.hasMatter();
    }

    public boolean hasAncMatter() {
        return null != ancExam && ancExam.hasMatter();
    }

    public boolean hasTfceMatter() {
        return null != tfceExam && tfceExam.hasMatter();
    }

    public boolean hasFloorMatter() {
        return null != floorExam && floorExam.hasMatter();
    }

    public boolean hasFnExam() {
        return null != fnExam && fnExam.hasExam();
    }

    public boolean hasSpExam() {
        return null != spExam && spExam.hasExam();
    }

    public boolean hasAncExam() {
        return null != ancExam && ancExam.hasExam();
    }

    public boolean hasTfceExam() {
        return null != tfceExam && tfceExam.hasExam();
    }

    public boolean hasFloorExam() {
        return null != floorExam && floorExam.hasExam();
    }

    public Long getFnDomain() {
        // return
        // FIN_OP_DISTRICT_TYPE_CODE.equalsIgnoreCase(session.getFirm().getFnDistrictTypeCode())
        // ?
        // FIN_OP_DOMAIN_ID : SP_DOMAIN_ID;
        Long retVal = 0L;
        if (session.getFirm().getFnDistrictCode().equals("M1")
                || session.getFirm().getFnDistrictCode().equals("M3"))
            retVal = FIN_OP_DOMAIN_ID;
        else if (session.getFirm().getFnDistrictCode().equals("TF")
                || session.getFirm().getFnDistrictCode().equals("EX")
                || session.getFirm().getFnDistrictCode().equals("TE"))
            retVal = TFCE_DOMAIN_ID;
        else
            retVal = SP_DOMAIN_ID;
        return retVal;
    }

    public Long getSpDomain() {
        // return
        // SP_DISTRICT_TYPE_CODE.equalsIgnoreCase(session.getFirm().getSpDistrictTypeCode())
        // ?
        // SP_DOMAIN_ID : FIN_OP_DOMAIN_ID;
        Long retVal = 0L;
        if (session.getFirm().getSpDistrictCode().equals("M1")
                || session.getFirm().getSpDistrictCode().equals("M3"))
            retVal = FIN_OP_DOMAIN_ID;
        else if (session.getFirm().getSpDistrictCode().equals("TF")
                || session.getFirm().getSpDistrictCode().equals("EX")
                || session.getFirm().getSpDistrictCode().equals("TE"))
            retVal = TFCE_DOMAIN_ID;
        else
            retVal = SP_DOMAIN_ID;
        return retVal;
    }

    public Long getAncDomain() {
        return ANC_DOMAIN_ID;
    }

    public Long getTfceDomain() {
        return TFCE_DOMAIN_ID;
    }

    public Long getFloorDomain() {
        return FLOOR_DOMAIN_ID;
    }

    public PsOutput getFnExam() {
        return fnExam;
    }

    public PsOutput getSpExam() {
        return spExam;
    }

    public PsOutput getAncExam() {
        return ancExam;
    }

    public PsOutput getTfceExam() {
        return tfceExam;
    }

    public PsOutput getFloorExam() {
        return floorExam;
    }

    public List<PsOutput> getEntries() {
        return (this.session != null ? this.session.getExams() : null);
    }

    public PsSession getSession() {
        return session;
    }

    public String getMarketTypeCode() {
        return marketTypeCode;
    }

    public int getVersion() {
        return session != null ? session.getFirm().getId().getVersionId() : 0;
    }

    public boolean isLinkFnAndSp() {
        // EXAM-10466: Exams should be linked if we have separate SP and FN
        // outputs & FN District Code != TF
        return linkFnAndSp && (getSession().getFirm().getFnDistrictCode() != null
                && !getSession().getFirm().getFnDistrictCode().equalsIgnoreCase("TF"));
    }

    public Map<Long, PsOutput> getFnAndSpDomainMap() {
        Map<Long, PsOutput> finOpAndSpDomains = new LinkedHashMap<>();
        if (hasFnExam()) {
            finOpAndSpDomains.put(getFnDomain(), getFnExam());
        }
        if (hasSpExam()) {
            finOpAndSpDomains.put(getSpDomain(), getSpExam());
        }
        return finOpAndSpDomains;
    }

    public Map<Long, PsOutput> getAncDomainMap() {
        Map<Long, PsOutput> ancDomainMap = new LinkedHashMap<>();
        if (hasAncExam()) {
            ancDomainMap.put(getAncDomain(), ancExam);
        }
        return ancDomainMap;
    }

    public Map<Long, PsOutput> getTfceDomainMap() {
        Map<Long, PsOutput> tfceDomainMap = new LinkedHashMap<>();
        if (hasTfceExam()) {
            tfceDomainMap.put(getTfceDomain(), tfceExam);
        }
        return tfceDomainMap;
    }

    public Map<Long, PsOutput> getFloorDomainMap() {
        Map<Long, PsOutput> floorDomainMap = new LinkedHashMap<>();
        if (hasFloorExam()) {
            floorDomainMap.put(getFloorDomain(), floorExam);
        }
        return floorDomainMap;
    }

    public void setFnExam(PsOutput fnExam) {
        this.fnExam = fnExam;
    }

    public void setSpExam(PsOutput spExam) {
        this.spExam = spExam;
    }

    public void setAncExam(PsOutput ancExam) {
        this.ancExam = ancExam;
    }

    public void setTfceExam(PsOutput tfceExam) {
        this.tfceExam = tfceExam;
    }

    public void setFloorExam(PsOutput floorExam) {
        this.floorExam = floorExam;
    }

    public void setLinkFnAndSp(boolean linkFnAndSp) {
        this.linkFnAndSp = linkFnAndSp;
    }
}