package org.finra.esched.service.rest.ui;

import org.finra.esched.domain.ui.PsSchedSessionView;

import java.util.List;

/**
 * Created by puppalaa on 7/24/2017.
 */
public class PsSchedSessionResponse {

    private List<ScheduleSessionResponse> sessionResponses;

    public PsSchedSessionResponse(){

    }

    public List<ScheduleSessionResponse> getSessionResponses() {
        return sessionResponses;
    }

    public void setSessionResponses(List<ScheduleSessionResponse> sessionResponses) {
        this.sessionResponses = sessionResponses;
    }
}
