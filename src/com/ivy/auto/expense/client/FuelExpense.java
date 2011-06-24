package com.ivy.auto.expense.client;

import java.io.Serializable;

public class FuelExpense implements Serializable{

	private static final long serialVersionUID = 1L;
	private String dateSubmimtted;
	private String distanceReading;
	private String volumeFilled;
	private String unitPrice;
	private String userName;
	
	public void setDateSubmimtted(String dateSubmimtted) {
		this.dateSubmimtted = dateSubmimtted;
	}
	
	public String getDateSubmimtted() {
		return dateSubmimtted;
	}
	
	public void setDistanceReading(String distanceReading) {
		this.distanceReading = distanceReading;
	}
	
	public String getDistanceReading() {
		return distanceReading;
	}
	
	public void setVolumeFilled(String volumeFilled) {
		this.volumeFilled = volumeFilled;
	}
	
	public String getVolumeFilled() {
		return volumeFilled;
	}
	
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
}
