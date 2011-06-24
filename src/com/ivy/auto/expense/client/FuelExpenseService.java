package com.ivy.auto.expense.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("fuel")
public interface FuelExpenseService extends RemoteService {
	String submitFuelData(String date, String distance, String volume, String price) throws IllegalArgumentException;
	String showFuelExpenses() throws IllegalArgumentException;
	String getUserName() throws IllegalArgumentException;
}
