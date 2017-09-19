package org.finra.esched.service.rest.ui;

import org.finra.exam.common.domain.ui.ErrorInfoJson;
import org.finra.exam.common.domain.ui.ReturnCodeJson;

import java.util.List;

/**
 * Created by puppalaa on 7/17/2017.
 */
public class ScheduleSessionResponse  extends ReturnCodeJson {

    private int sssnId;

    public List<ErrorInfoJson> errorInfoJsonList;

    public List<ErrorInfoJson> getErrorInfoJsonList() {
        return errorInfoJsonList;
    }

    public void setErrorInfoJsonList(List<ErrorInfoJson> errorInfoJsonList) {
        this.errorInfoJsonList = errorInfoJsonList;
    }

    public int getSssnId() {
        return sssnId;
    }

    public void setSssnId(int sssnId) {
        this.sssnId = sssnId;
    }
}
