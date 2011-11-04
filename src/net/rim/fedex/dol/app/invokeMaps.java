package net.rim.fedex.dol.app;

import net.rim.blackberry.api.invoke.*;
import javax.microedition.location.*;

public class invokeMaps
{
    public invokeMaps (AddressInfo[] addressArray)
    {
    	
        Landmark[] landMarks = new Landmark[addressArray.length];
        
        for(int i=0 ; i< addressArray.length ; ++i )
        	landMarks[i] = new Landmark(addressArray[i].getField(AddressInfo.STREET), "violation", null, addressArray[i]);

        MapsArguments ma = new MapsArguments(landMarks);
        Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, ma);
  }
}