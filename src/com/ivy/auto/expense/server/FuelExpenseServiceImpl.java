package com.ivy.auto.expense.server;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ivy.auto.expense.client.FuelExpense;
import com.ivy.auto.expense.client.FuelExpenseService;
import com.ivy.auto.expense.shared.FieldVerifier;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class FuelExpenseServiceImpl extends RemoteServiceServlet implements
		FuelExpenseService {

	FuelExpense fuelExpense = new FuelExpense();
	FuelExpenseDAO fuelExpenseDao = new FuelExpenseDAOImpl();
	ObjectMapper mapper;
	
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	@Override
	public String submitFuelData(String date, String distance, String volume,
			String price) throws IllegalArgumentException {

		fuelExpense.setDateSubmimtted(date);
		fuelExpense.setDistanceReading(distance);
		fuelExpense.setVolumeFilled(volume);
		fuelExpense.setUnitPrice(price);
		fuelExpense.setUserName(this.getUserName());
		
		fuelExpenseDao.addExpense(fuelExpense);

		return "Fuel data stored succesfully";
	}

	@Override
	public String showFuelExpenses() throws IllegalArgumentException {
		
		return getJsonStringFromObject(fuelExpenseDao.getAllExpenses(getUserName()));
	}
	
	@Override
	public String getUserName() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String userName = "";
		if (user != null) {
			userName = user.getEmail();
		}
		return userName;
	}

	String getJsonStringFromObject(List<FuelExpense> list){
		mapper = new ObjectMapper();
		String outStr = "";
		try {
			outStr = mapper.writeValueAsString(list);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outStr;
	}
}
