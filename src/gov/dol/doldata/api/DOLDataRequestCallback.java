package gov.dol.doldata.api;

import java.util.Vector;

public interface DOLDataRequestCallback {
	//Return results
	public void DOLDataResultsCallback(Vector results);
	
	//Error Callback
	public void DOLDataErrorCallback(String error);
}
