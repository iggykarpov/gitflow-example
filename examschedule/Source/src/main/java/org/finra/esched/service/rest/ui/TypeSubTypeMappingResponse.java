package org.finra.esched.service.rest.ui;

import org.finra.esched.domain.PsTypeSubTypeComponentMap;
import org.finra.esched.domain.PsTypeSubTypeComponentMapView;
import org.finra.exam.common.domain.ui.ReturnCodeJson;

import java.util.List;


public class TypeSubTypeMappingResponse extends ReturnCodeJson {

	public TypeSubTypeMappingResponse(){

	}

	private int total;
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

    private List<PsTypeSubTypeComponentMapView> mapping;

	public List<PsTypeSubTypeComponentMapView> getMapping() {
		return mapping;
	}

	public void setMapping(List<PsTypeSubTypeComponentMapView> mapping) {
		this.mapping = mapping;
	}
}
