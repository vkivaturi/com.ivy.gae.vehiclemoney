package com.ivy.auto.expense.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ivy.auto.expense.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AutoExpenseTrackerAdvanced implements EntryPoint {
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
	final Button sendButton = new Button("Submit Expense");
	final Button showButton = new Button("Show Expenses");

	final Label userNameLabel = new Label();
	final Label summaryDistancePerDayLabel = new Label();
	final Label summaryVolumePerDayLabel = new Label();
	final Label summaryFuelPriceLabel = new Label();

	final TextBox dateField = new TextBox();
	final TextBox distanceField = new TextBox();
	final TextBox fuelField = new TextBox();
	final TextBox priceField = new TextBox();
	DatePicker datePicker = new DatePicker();
	Grid resultGrid;
	final ScrollPanel scrollPanel = new ScrollPanel();
	final HTML serverResponseLabel = new HTML();
	final Label errorLabel = new Label();

	Date today = new Date();
	DateTimeFormat sdf;

	public void onModuleLoad() {

		manageDateFields();

		// Fetch user name using helper service. This is an asynchronous call
		ClientHelper.setUserName(fuelExpenseService, userNameLabel);

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");
		showButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
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

				if (resultGrid != null) {
					scrollPanel.remove(resultGrid);
				}

				if (scrollPanel != null) {
					RootPanel.get("resultGridContainer").remove(scrollPanel);
				}

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

				if (!FieldVerifier.isValidInt(distanceField.getText())) {
					errorLabel
							.setText("Please enter a valid integer > 0 for distance reading");
					return;
				}

				if (!FieldVerifier.isValidInt(fuelField.getText())) {
					errorLabel
							.setText("Please enter a valid integer > 0 for volume of fuel filled");
					return;
				}

				if (!FieldVerifier.isValidInt(priceField.getText())) {
					errorLabel
							.setText("Please enter a valid integer > 0 for unit price of fuel");
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
								resetInputFields();
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
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
								resetInputFields();
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
								resetInputFields();
								manageGrid(result);
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

	private void manageDateFields() {
		// Manage date field
		sdf = DateTimeFormat.getFormat("dd-MMM-yyyy");
		dateField.setText(sdf.format(today));
		dateField.setEnabled(false);

		// Set the value in the text box when the user selects a date
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			public void onValueChange(ValueChangeEvent event) {
				Date date = (Date) event.getValue();
				dateField.setText(sdf.format(date));
			}
		});

		// Set the default value
		datePicker.setValue(new Date(), true);

		// Add the widgets to the page
		RootPanel.get("dateFieldContainer").add(dateField);
		RootPanel.get("dateFieldContainer").add(datePicker);
	}

	private void manageGrid(String result) {

		// Extract JSON objects using GWT APIs

		// Step 1 - Get JSONValue using static parser
		JSONValue jsonMasterVal = JSONParser.parseLenient(result);

		// Step 2 - Convert the JSONValue into an array since we know the server
		// returns array list of expenses
		JSONArray jsonMainArray = jsonMasterVal.isArray();

		int arrSize = jsonMainArray.size();
		long firstDateReading = 0;
		Date currentDateReading;

		long lastDistanceReading = 0;
		long currentDistanceReading = 0;

		long fuelTotalConsumed = 0;
		long fuelTotalPrice = 0;

		resultGrid = new Grid(arrSize + 1, 4);
		resultGrid.setWidth("100%");
		resultGrid.setBorderWidth(10);

		manageGridHeader(resultGrid);

		// Step 3 - Iterate through the array
		for (int i = 0; i < arrSize; i++) {

			// Step 4 - Extract individual JSON value
			JSONValue jsonDetailedVal = jsonMainArray.get(i);

			// Step 5 - Convert JSON value into an object (this represents the
			// FuelExpense record)
			JSONObject jsonObject = jsonDetailedVal.isObject();

			// Row number is incremented by 1 since there is a header row
			resultGrid.setText(i + 1, 0, jsonObject.get("dateSubmimtted")
					.isString().stringValue());
			resultGrid.setText(i + 1, 1, jsonObject.get("distanceReading")
					.isString().stringValue());
			resultGrid.setText(i + 1, 2, jsonObject.get("volumeFilled")
					.isString().stringValue());
			resultGrid.setText(i + 1, 3, jsonObject.get("unitPrice").isString()
					.stringValue());

			currentDateReading = sdf.parse(jsonObject.get("dateSubmimtted")
					.isString().stringValue());
			if ((firstDateReading == 0)
					|| (firstDateReading > currentDateReading.getTime())) {
				firstDateReading = currentDateReading.getTime();
			}

			try {
				currentDistanceReading = Long.valueOf(jsonObject
						.get("distanceReading").isString().stringValue());
			} catch (Exception e) {
				// Record found with no entry for distance reading
				currentDistanceReading = 0;
			}
			if ((lastDistanceReading == 0)
					|| (lastDistanceReading < currentDistanceReading)) {
				lastDistanceReading = currentDistanceReading;
			}

			try {
				fuelTotalConsumed = fuelTotalConsumed
						+ Long.valueOf(jsonObject.get("volumeFilled")
								.isString().stringValue());
			} catch (NumberFormatException e) {
				// Do nothing - this is to handle null value
			}
			try {
				fuelTotalPrice = fuelTotalPrice
						+ Long.valueOf(jsonObject.get("unitPrice").isString()
								.stringValue());
			} catch (NumberFormatException e) {
				// Do nothing - this is to handle null value
			}
		}

		manageSummarySection(firstDateReading, lastDistanceReading,
				fuelTotalConsumed, fuelTotalPrice);

		scrollPanel.add(resultGrid);
		scrollPanel.setSize("600px", "300px");
		RootPanel.get("resultGridContainer").add(scrollPanel);

	}

	private void manageSummarySection(long firstDateReading,
			long lastDistanceReading, long fuelTotalConsumed,
			long fuelTotalPrice) {
		long totalTravelDays = (System.currentTimeMillis() - firstDateReading)
				/ (1000 * 60 * 60 * 24);
		summaryDistancePerDayLabel.setText("Average distance travelled is "
				+ lastDistanceReading / totalTravelDays + " Kms/day");
		summaryVolumePerDayLabel.setText("Average fuel consumed is "
				+ fuelTotalConsumed / totalTravelDays + " ltr/day");

		summaryFuelPriceLabel.setText("Average fuel price spent is "
				+ fuelTotalPrice / totalTravelDays + " Rs/day");

		RootPanel
				.get("kmspd")
				.getElement()
				.setPropertyInt("value",
						(int) (lastDistanceReading / totalTravelDays));
		RootPanel
				.get("ltrpd")
				.getElement()
				.setPropertyInt("value",
						(int) (fuelTotalConsumed / totalTravelDays));
		RootPanel
				.get("rpspd")
				.getElement()
				.setPropertyInt("value",
						(int) (fuelTotalPrice / totalTravelDays));

		showGaugeVisualization();
		showTableVisualization();
	}

	private void manageGridHeader(Grid resultGrid2) {
		resultGrid.setText(0, 0, "Expense Date");
		resultGrid.setText(0, 1, "Vehicle Distance Reading");
		resultGrid.setText(0, 2, "Volume Filled");
		resultGrid.setText(0, 3, "Unit Price");
	}

	private void resetInputFields() {
		distanceField.setText("");
		manageDateFields();
		fuelField.setText("");
		priceField.setText("");
		serverResponseLabel.setHTML("");
		errorLabel.setText("");
	}

	native void showGaugeVisualization() /*-{

		// ...implemented with JavaScript
		$wnd.drawVisualization();
	}-*/;

	native void showTableVisualization() /*-{

		// ...implemented with JavaScript
		$wnd.drawTable();
	}-*/;

}
