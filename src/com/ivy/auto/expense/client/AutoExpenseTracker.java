package com.ivy.auto.expense.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ivy.auto.expense.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AutoExpenseTracker implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final FuelExpenseServiceAsync fuelExpenseService = GWT
			.create(FuelExpenseService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton = new Button("Submit Expense");
		final Button showButton = new Button("Show Expenses");
		
		final Label userNameLabel = new Label();

		final TextBox dateField = new TextBox();
		final TextBox distanceField = new TextBox();
		final TextBox fuelField = new TextBox();
		final TextBox priceField = new TextBox();

		Date today = new Date();
		DateTimeFormat sdf = DateTimeFormat.getFormat("dd-MMM-yyyy");

		dateField.setText(sdf.format(today));
		dateField.setEnabled(false);

		//Fetch user name using helper service. This is an asynchronous call
		ClientHelper.setUserName(fuelExpenseService, userNameLabel);
		
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");
		showButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("dateFieldContainer").add(dateField);
		RootPanel.get("distanceFieldContainer").add(distanceField);
		RootPanel.get("fuelFieldContainer").add(fuelField);
		RootPanel.get("priceFieldContainer").add(priceField);

		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("showButtonContainer").add(showButton);

		RootPanel.get("errorLabelContainer").add(errorLabel);
		RootPanel.get("userNameContainer").add(userNameLabel);

		// Focus the cursor on the name field when the app loads
		distanceField.setFocus(true);
		distanceField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class ExpenseSubmitHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendFuelDataToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendFuelDataToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendFuelDataToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = dateField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter a valid date");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				fuelExpenseService.submitFuelData(dateField.getText(),
						distanceField.getText(), fuelField.getText(),
						priceField.getText(), new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});

//				fuelExpenseService.getUserName(new AsyncCallback<String>() {
//					public void onFailure(Throwable caught) {
//						// Show the RPC error message to the user
//						System.out.println("Error calling user name");
//					}
//
//					@Override
//					public void onSuccess(String result) {
//						System.out.println("getUserName " + result);
//					}
//
//				});

			}
		}

		// Add a handler to send the name to the server
		ExpenseSubmitHandler submitHandler = new ExpenseSubmitHandler();
		sendButton.addClickHandler(submitHandler);
		distanceField.addKeyUpHandler(submitHandler);

		// Expense retrieval process
		// Create a handler for the sendButton and nameField
		class ShowExpensesHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {

				serverResponseLabel.setText("");
				fuelExpenseService
						.showFuelExpenses(new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
		}
		ShowExpensesHandler showHandler = new ShowExpensesHandler();
		showButton.addClickHandler(showHandler);
	}
}
