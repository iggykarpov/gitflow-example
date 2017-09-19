package org.finra.esched.service.rest.ui;

import org.codehaus.jackson.map.ObjectMapper;
import org.finra.esched.domain.ui.PsSchedSessionView;

import java.util.List;

/**
 * @author RuzhaV
 */

public class PsSchedSessionRequest {
	private List<PsSchedSessionView> sessions;

	public PsSchedSessionRequest(){
		
	}
	public PsSchedSessionRequest(List<PsSchedSessionView> sessions) {
		this.sessions = sessions;
	}

	public List<PsSchedSessionView> getSessions() {
		return sessions;
	}

	public void setSessions(List<PsSchedSessionView> sessions) {
		this.sessions = sessions;
	}

	public static PsSchedSessionRequest map(String jsonString) {

		if (jsonString != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.readValue(jsonString, PsSchedSessionRequest.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return null;
	}

}
