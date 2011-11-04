package gov.dol.doldata.api;

import gov.dol.doldata.util.DOLAPIUtils;
import gov.dol.doldata.util.HttpConnectionFactory;
import gov.dol.doldata.util.NoMoreTransportsException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.ui.UiApplication;

public class DOLDataRequest {

	// instance variables
	private DOLDataRequestCallback callback;
	private DOLDataContext context;

	/**
	 * @return the context
	 */
	public DOLDataContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(DOLDataContext context) {
		this.context = context;
	}

	public DOLDataRequestCallback getCallback() {
		return callback;
	}

	/**
	 * Constructor
	 * 
	 * @param callback
	 * @param context
	 */
	public DOLDataRequest(DOLDataRequestCallback callback,
			DOLDataContext context) {
		super();
		this.callback = callback;
		this.context = context;
	}

	public void setCallback(DOLDataRequestCallback callback) {
		this.callback = callback;
	}

	public void callAPIMethod(String method, Hashtable arguments) {
		StringBuffer url = new StringBuffer(context.getApiHost()
				+ context.getApiURI() + "/" + method);
		StringBuffer queryString = new StringBuffer();

		// Enumerate the arguments and add them to the request
		if (arguments != null) {
			Enumeration enum = arguments.keys();
			while (enum.hasMoreElements()) {
				String key = (String) enum.nextElement();

				// Store value. URLEncode is needed.
				String value = DOLAPIUtils.urlEncode((String) arguments
						.get(key));

				if (key.equals("top") || key.equals("skip")
						|| key.equals("select") || key.equals("orderby")
						|| key.equals("filter")) {
					// If it is the first parameter we append a ?, if not we
					// concatenate with &
					if (queryString.length() == 0) {
						queryString.append("?");
					} else {
						queryString.append("&");
					}
					// append the querystring key and value
					queryString.append("$" + key + "=" + value);
				}
			}
		}

		// If there are valid arguments then append it to the URL
		if (queryString.length() > 0) {
			url.append(queryString.toString());
		}

		// Make request to API. Will run on separate thread.
		makeHTTPRequestAsync(url.toString());
	}

	private void makeHTTPRequestAsync(final String url) {
		Runnable r = new Runnable() {
			public void run() {
				// Make the request
				HttpConnectionFactory factory = new HttpConnectionFactory(url,
						HttpConnectionFactory.TRANSPORTS_ANY);

				// Authorization Header
				String authHeader = "";

				try {
					authHeader = DOLAPIUtils.getRequestHeader(url,
							context.getApiHost(), context.getApiKey(),
							context.getApiSecret());
				} catch (final Exception e) {
					// Send error to callback
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {
								public void run() {
									callback.DOLDataErrorCallback(e
											.getMessage());
								}
							});
					return;
				}

				// Try all transports
				while (true) {
					try {
						HttpConnection connection = factory.getNextConnection();
						try {
							// Set Headers
							connection.setRequestMethod("GET");
							connection.setRequestProperty("Accept",
									"application/json");
							connection.setRequestProperty("Authorization",
									authHeader);

							InputStream is = connection.openInputStream();

							final byte[] data = IOUtilities.streamToBytes(is);

							if (is != null) {
								is.close();
							}

							// Check the response code
							int responseCode = connection.getResponseCode();
							
							// Close the connection
							if (connection != null) {
								connection.close();
							}
							
							if (responseCode != 200) {
								String errorMessage;

								switch (responseCode) {
								case 401:
									errorMessage = "Unauthorized";
									break;
								case 400:
									errorMessage = "Bad Request";
									break;
								case 404:
									errorMessage = "Request not found";
									break;
								default:
									errorMessage = "Error " + responseCode
											+ " returned";
									break;
								}
								callbackWithError(errorMessage);
							}

							// Return results to the callback
							callbackWithResults(new String(data));
							break;
						} catch (IOException e) {
							// Log the error or store it for displaying to
							// the end user if no transports succeed
						}
					} catch (NoMoreTransportsException e) {
						// There are no more transports to attempt
						callbackWithError("Unable to perform request");
						break;
					}
				}
			}
		};
		// Start request thread
		new Thread(r).start();
	}

	private void callbackWithResults(final String results) {
		// Return results to the callback
		final Vector parsedResults = DOLAPIUtils.parseJSON(results);
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				callback.DOLDataResultsCallback(parsedResults);
			}
		});
	}

	private void callbackWithError(final String error) {
		// Return error to the callback
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				callback.DOLDataErrorCallback(error);
			}
		});
	}
}
