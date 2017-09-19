package org.finra.esched.service.rest.ui;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

public class PsRequest implements Serializable {
	
	private final static String[] DATE_FORMATS = new String[] {"MM/dd/yyyy"};
	
	 String getValue (String thisValue, String value)
	{
		if (StringUtils.isBlank(value))
			return null;
		else
			return value;
	}

	Date getValue (Date thisValue, String thatValue)
	{
		if (StringUtils.isBlank(thatValue))
			return null;
		else
		{
			try
			{
				return DateUtils.parseDate(thatValue, DATE_FORMATS);
			}
			catch (Exception e)
			{
				throw new RuntimeException ("Date '" + thatValue + "' value is wrong", e);
			}
		}
	}
	
	Long getValue (Long thisValue, String thatValue)
	{
		if (StringUtils.isBlank(thatValue))
			return null;
		else
			return Long.valueOf(thatValue);
	}
}
