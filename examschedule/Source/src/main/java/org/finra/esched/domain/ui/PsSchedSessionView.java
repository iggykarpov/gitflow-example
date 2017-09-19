package org.finra.esched.domain.ui;

public class PsSchedSessionView {
	
	
	private int sssnId;
	
	public PsSchedSessionView() {

	}

	public PsSchedSessionView(int sessionId){
		this();
		this.sssnId=sessionId;

	}

	public int getSssnId() {
		return sssnId;
	}

	public void setSssnId(int sessionId) {
		this.sssnId = sessionId;
	}

	

}
