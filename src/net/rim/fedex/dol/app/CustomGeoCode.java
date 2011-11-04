package net.rim.fedex.dol.app;

import java.util.Vector;

import javax.microedition.location.AddressInfo;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Landmark;

import net.rim.device.api.lbs.Locator;
import net.rim.device.api.lbs.LocatorException;
import net.rim.device.api.lbs.maps.server.Geocoder;
import net.rim.device.api.lbs.maps.server.exchange.GeocodeExchange;

public class CustomGeoCode {
	
	private Thread geocodeThread;
	
	private AddressInfo[] addressInfo; 
	
	private Landmark[][] landMarkResults; 
	
	public CustomGeoCode(AddressInfo[] addressInfoArray)
	{ 
		geocodeThread = new Thread(thread);
		addressInfo = addressInfoArray;
		geocodeThread.setPriority(Thread.MIN_PRIORITY);
		geocodeThread.start();
		
	}
	
	public 
	Runnable thread = new Runnable()
	{
	    public void run()
	    {

	        landMarkResults = new Landmark[addressInfo.length][];
	    	Coordinates coord = new Coordinates(35.46, -78.39, Float.NaN);

	        try
	        {
	            for (int i=0 ; i < addressInfo.length ; ++i )
	            	landMarkResults[i] = Locator.geocode(addressInfo[i], coord);
	        }
	        catch ( LocatorException lex )
	        {
	        }
	    }
	};

}
