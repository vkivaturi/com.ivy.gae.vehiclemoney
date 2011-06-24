package com.ivy.auto.expense.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface FuelExpenseServiceAsync {
	void submitFuelData(String date, String distance, String volume,
			String price, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void showFuelExpenses(AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getUserName(AsyncCallback<String> callback)
			throws IllegalArgumentException;

}
