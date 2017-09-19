package org.finra.esched.domain;

import java.io.Serializable;
import java.util.List;

public class PsTtmContactResponse implements Serializable {

	List<PsTtmContact> contactList;

	public List<PsTtmContact> getContactList() {
		return contactList;
	}

	public void setContactList(List<PsTtmContact> contactList) {
		this.contactList = contactList;
	}
	

} 