package org.finra.esched.service.rest.ui;

import java.util.HashMap;
import java.util.Map;


/**
 * @author RuzhaV
 */

public class PrescheduleRequest extends PsRequest{
    private Long pageSize;
    private Long pageNumber;
    
    private Map<String, String> smartFilters = new HashMap<String, String>();
    
    private String crdFilter;
    private String impactFilter;
    private String riskFilter;
    private String districtFilter;
    private String statusFilter;
    private String ovrrdFilter;
    private String hasRqFilter;
    private String spFilter;
    private String muniFilter;
    private String optionsFilter;
    private String ffnFilter;
    private String fnFilter;
    private String flFilter;
    private String ancFilter;
    private String rsaFnFilter;
    private String muniAdvFilter;
    private String rsaSpFilter;
    private String sdfFilter;    
    
    public PrescheduleRequest(
    		String pageSize, 
    		String pageNumber, 
    		String crdFilter,
			String impactFilter,
			String riskFilter,
			String districtFilter,
			String statusFilter,
			String ovrrdFilter,
			String hasRqFilter,
			String spFilter,
			String muniFilter,
			String optionsFilter,
			String ffnFilter,
			String fnFilter,
			String flFilter,
			String ancFilter) {
		this.pageSize=getValue(this.pageSize, pageSize);
		this.pageNumber=getValue(this.pageNumber, pageNumber);
		this.crdFilter=getValue(this.crdFilter, crdFilter);
		this.impactFilter=getValue(this.impactFilter, impactFilter);
		this.riskFilter=getValue(this.riskFilter, riskFilter);
		this.districtFilter=getValue(this.districtFilter, districtFilter);
		this.statusFilter=getValue(this.statusFilter, statusFilter);
		this.ovrrdFilter=getValue(this.ovrrdFilter, ovrrdFilter);
		this.hasRqFilter=getValue(this.hasRqFilter, hasRqFilter);
		this.spFilter=getValue(this.spFilter, spFilter);
		this.muniFilter=getValue(this.muniFilter, muniFilter);
		this.optionsFilter=getValue(this.optionsFilter, optionsFilter);
		this.ffnFilter=getValue(this.ffnFilter, ffnFilter);
		this.fnFilter=getValue(this.fnFilter, fnFilter);
		this.flFilter=getValue(this.flFilter, flFilter);
		this.ancFilter=getValue(this.ancFilter, ancFilter);
		
	}

	public Long getPageSize() {
		return pageSize;
	}
	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public Long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getCrdFilter() {
		return crdFilter;
	}

	public void setCrdFilter(String crdFilter) {
		this.crdFilter = crdFilter;
	}

	public String getImpactFilter() {
		return impactFilter;
	}

	public void setImpactFilter(String impactFilter) {
		this.impactFilter = impactFilter;
	}

	public String getRiskFilter() {
		return riskFilter;
	}

	public void setRiskFilter(String riskFilter) {
		this.riskFilter = riskFilter;
	}

	public String getDistrictFilter() {
		return districtFilter;
	}

	public void setDistrictFilter(String districtFilter) {
		this.districtFilter = districtFilter;
	}

	public String getStatusFilter() {
		return statusFilter;
	}

	public void setStatusFilter(String statusFilter) {
		this.statusFilter = statusFilter;
	}

	public String getOvrrdFilter() {
		return ovrrdFilter;
	}

	public void setOvrrdFilter(String ovrrdFilter) {
		this.ovrrdFilter = ovrrdFilter;
	}

	public String getHasRqFilter() {
		return hasRqFilter;
	}

	public void setHasRqFilter(String hasRqFilter) {
		this.hasRqFilter = hasRqFilter;
	}

	public String getSpFilter() {
		return spFilter;
	}

	public void setSpFilter(String spFilter) {
		this.spFilter = spFilter;
	}

	public String getMuniFilter() {
		return muniFilter;
	}

	public void setMuniFilter(String muniFilter) {
		this.muniFilter = muniFilter;
	}

	public String getOptionsFilter() {
		return optionsFilter;
	}

	public void setOptionsFilter(String optionsFilter) {
		this.optionsFilter = optionsFilter;
	}

	public String getFfnFilter() {
		return ffnFilter;
	}

	public void setFfnFilter(String ffnFilter) {
		this.ffnFilter = ffnFilter;
	}

	public String getFnFilter() {
		return fnFilter;
	}

	public void setFnFilter(String fnFilter) {
		this.fnFilter = fnFilter;
	}

	public String getFlFilter() {
		return flFilter;
	}

	public void setFlFilter(String flFilter) {
		this.flFilter = flFilter;
	}

	public String getAncFilter() {
		return ancFilter;
	}

	public void setAncFilter(String ancFilter) {
		this.ancFilter = ancFilter;
	}

	public String getRsaFnFilter() {
		return rsaFnFilter;
	}

	public void setRsaFnFilter(String rsaFnFilter) {
		this.rsaFnFilter = rsaFnFilter;
	}

	public String getMuniAdvFilter() {
		return muniAdvFilter;
	}

	public void setMuniAdvFilter(String muniAdvFilter) {
		this.muniAdvFilter = muniAdvFilter;
	}

	public String getRsaSpFilter() {
		return rsaSpFilter;
	}

	public void setRsaSpFilter(String rsaSpFilter) {
		this.rsaSpFilter = rsaSpFilter;
	}

	public String getSdfFilter() {
		return sdfFilter;
	}

	public void setSdfFilter(String sdfFilter) {
		this.sdfFilter = sdfFilter;
	}
    
}
