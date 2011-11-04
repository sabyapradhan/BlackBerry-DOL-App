package net.rim.fedex.dol.app;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.location.AddressInfo;

import gov.dol.doldata.api.DOLDataContext;
import gov.dol.doldata.api.DOLDataRequest;
import gov.dol.doldata.api.DOLDataRequestCallback;
import net.rim.device.api.lbs.Locator;
import net.rim.device.api.lbs.maps.MapDimensions;
import net.rim.device.api.lbs.maps.model.MapDataModel;
import net.rim.device.api.lbs.maps.server.Geocoder;
import net.rim.device.api.lbs.maps.server.exchange.GeocodeExchange;
import net.rim.device.api.lbs.maps.ui.MapField;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.MultiMap;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class MyScreen extends MainScreen implements FieldChangeListener , DOLDataRequestCallback
{
	
	private DOLDataContext context;
	
	private ButtonField submitButton;
	
	private ButtonField clearButton;
	
	private BasicEditField editField; 
	
	private ObjectChoiceField datasetChoiceField;
	
	private String currentCityNameEntered;
	
	private String currentDatasetSelected; 
 
	private GridFieldManager grid;
	
	private LabelField labelField; 
	
	private ButtonField showOnMap;
	
	// Data Set Constants
	
	private static String FOOD_SERVICE = "Food Service";
	
	private static String HOSPITALITY = "Hospitality";
	
	private static String RETAIL = "Retail";
	
	private String[] dataSetChoices = {FOOD_SERVICE,HOSPITALITY,RETAIL};
	
	private MultiMap mapNamesAndDatasets; 
	
	// for mapping
	
	private AddressInfo[] addressInfo; 
    /**
     * Creates a new MyScreen object
     */
    public MyScreen()
    {
    	
    	//invokeMaps maps = new invokeMaps();
    	
    	mapNamesAndDatasets = new MultiMap();
    	mapNamesAndDatasets.add(FOOD_SERVICE, "foodService");
    	mapNamesAndDatasets.add(HOSPITALITY,"hospitality");
    	mapNamesAndDatasets.add(RETAIL, "retail");
    	
    	context = new DOLDataContext(SecurityConstants.API_KEY, SecurityConstants.SHARED_SECRET, SecurityConstants.API_HOST, SecurityConstants.API_URI);
        // Set the displayed title of the screen       
        setTitle("Dept of Labor - Violation Finder");
        
        VerticalFieldManager container = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL);
        
        HorizontalFieldManager buttons = new HorizontalFieldManager(FIELD_HCENTER);
        
        HorizontalFieldManager labelAndText = new HorizontalFieldManager();
        
        labelField = new LabelField("Enter City Name                      ",FIELD_LEFT);
        
        editField = new CustomEditField("", "Raleigh", 20, FIELD_RIGHT);//new EditField("Enter City name", "Raleigh",15,Field.FIELD_RIGHT);
        
        labelAndText.add(labelField);
        labelAndText.add(editField);

        submitButton = new ButtonField("Search" , ButtonField.CONSUME_CLICK | ButtonField.FIELD_HCENTER);
        
        clearButton = new ButtonField("Clear",ButtonField.CONSUME_CLICK | ButtonField.FIELD_HCENTER);
        
        showOnMap = new ButtonField("Show on Map",ButtonField.CONSUME_CLICK | ButtonField.FIELD_HCENTER);
        
        submitButton.setChangeListener(this);
        
        clearButton.setChangeListener(this);
        
        showOnMap.setChangeListener(this);
        
        // the grid
        
        grid = new GridFieldManager(21, 2, 0);
        
        grid.setColumnPadding(20);
        grid.setRowPadding(20);
        
        // add the drop down list for      
        int iSetTo = 2;
        datasetChoiceField = new ObjectChoiceField("Choose Data Set",dataSetChoices,iSetTo); 
        container.add(datasetChoiceField);
        //container.add(editField);
        //add(datasetChoiceField);
        
        //add(editField);
        //add(submitButton);
        //add(clearButton);
        buttons.add(submitButton);
        buttons.add(clearButton);
        buttons.add(showOnMap);
        container.add(labelAndText);
        container.add(buttons);
        add(container);
        add(grid);
        
        
    }
	public void DOLDataResultsCallback(Vector results) {
		
		grid.insert(new LabelField("Name of Establishment"), 0, 0,Field.FIELD_LEFT);
		grid.insert(new LabelField("Violations"), 0,1,FIELD_LEFT);
		
		final FontFamily fontFamily[] = FontFamily.getFontFamilies();
	    final Font font10 = fontFamily[1].getFont(FontFamily.CBTF_FONT, 15);
	    
	    addressInfo = new AddressInfo[results.size()];
	    
		for(int i =0 ; i< results.size(); ++i)
		{
			 	Hashtable record = (Hashtable)results.elementAt(i);
			 	
			 	//LabelField label = new LabelField(record.get("estab_name")+ " -  " + record.get("total_violations"),FOCUSABLE );
			 	String temp = (String)record.get("estab_name");
			 	LabelField field = new LabelField(temp);
			 	field.setFont(font10);
			 	grid.insert(field, i+1, 0,Field.FIELD_LEFT);
			 	grid.insert(new LabelField(record.get("total_violations").toString()), i+1, 1,Field.FIELD_HCENTER);
			 	//populate address info
			 	AddressInfo curAddressInfo = new AddressInfo();
			 	curAddressInfo.setField(AddressInfo.STREET, (String) record.get("site_address"));
			 	curAddressInfo.setField(AddressInfo.CITY, (String) record.get("site_city"));
			 	curAddressInfo.setField(AddressInfo.STATE, (String) record.get("site_state"));
			 	curAddressInfo.setField(AddressInfo.POSTAL_CODE, (String) record.get("site_zip"));
			 	curAddressInfo.setField(AddressInfo.COUNTRY, "United States");
			 	
			 	addressInfo[i] = curAddressInfo;
			 	
		}
		
		setStatus(null);
		
	}
	public void DOLDataErrorCallback(String error) {
		
		Dialog.alert(error);
		
		setStatus(null);
		
	}

	public void fieldChanged(Field field, int con) {
            
		if (field == clearButton) {
			clearInputSettings();
			grid.deleteAll();
		} else if (field == submitButton) {
			clearInputSettings();
			grid.deleteAll();
			LabelField status = new LabelField("Searching ... ",
					LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
			setStatus(status);

			int selectedIndex = datasetChoiceField.getSelectedIndex();
			currentDatasetSelected = dataSetChoices[selectedIndex];

			String dataSetName = (String) mapNamesAndDatasets.elements(
					currentDatasetSelected).nextElement();

			currentCityNameEntered = editField.getText();
			if (currentCityNameEntered == null
					|| currentCityNameEntered.length() == 0) {
				// TODO
				// some kind of pop up or something ..
			}

			try {
				DOLDataRequest req = new DOLDataRequest(this, context);

				String method = "Compliance/OSHA/" + dataSetName;

				Hashtable table = new Hashtable();

				table.put("top", "20");
				table.put(
						"filter",
						"(site_city eq '"
								+ currentCityNameEntered.toUpperCase()
								+ "') and (total_violations gt 0)");

				req.callAPIMethod(method, table);
			} catch (Exception e) {
				Dialog.alert(e.getMessage());

				setStatus(null);
			}
		} else if (field == showOnMap) {
			// invoke maps
            if(addressInfo.length != 0)
            {
            	new invokeMaps(addressInfo);
            }
		}

	}
	
	public boolean onSavePrompt()
	{
	    return true;
	}
	

}
