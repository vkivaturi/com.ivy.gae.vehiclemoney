package com.ivy.auto.expense.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;


public class ClientHelper {
	static void setUserName(FuelExpenseServiceAsync fuelExpenseService, final Label userNameLabel){
		fuelExpenseService.getUserName(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				System.out.println("Error calling user name");
			}

			@Override
			public void onSuccess(String result) {
				userNameLabel.setText(result);
			}

		});
	}
}
