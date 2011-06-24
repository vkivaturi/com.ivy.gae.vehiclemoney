package com.ivy.auto.expense.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.ivy.auto.expense.client.FuelExpense;

public class FuelExpenseDAOImpl implements FuelExpenseDAO{

	Key fuelExpenseKey; 
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();;
	
	@Override
	public void addExpense(FuelExpense fuelExpenseInput) {
		
		fuelExpenseKey = KeyFactory.createKey("FuelExpense", System.currentTimeMillis());
		Entity fuelExpenseEntity = new Entity("FuelExpense", fuelExpenseKey);
		
		fuelExpenseEntity.setProperty("date", fuelExpenseInput.getDateSubmimtted());
		fuelExpenseEntity.setProperty("distance", fuelExpenseInput.getDistanceReading());
		fuelExpenseEntity.setProperty("volume", fuelExpenseInput.getVolumeFilled());
		fuelExpenseEntity.setProperty("unitprice", fuelExpenseInput.getUnitPrice());
		fuelExpenseEntity.setProperty("userName", fuelExpenseInput.getUserName());

		datastore.put(fuelExpenseEntity);
	}

	@Override
	public List<FuelExpense> getAllExpenses(String inpUser) {
		List<FuelExpense> fuelList = new ArrayList<FuelExpense>();
		Iterator<Entity> itrEntity;
		Entity entity;
		FuelExpense fuelRec = new FuelExpense();
		Query query = new Query("FuelExpense");
		query.addFilter("userName", Query.FilterOperator.EQUAL, inpUser);
		List<Entity> list = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
		
		itrEntity = list.iterator();
		
		while(itrEntity.hasNext()){
			entity = itrEntity.next();
			fuelRec = new FuelExpense();

			fuelRec.setDateSubmimtted((String) entity.getProperty("date"));
			fuelRec.setDistanceReading((String) entity.getProperty("distance"));
			fuelRec.setVolumeFilled((String) entity.getProperty("volume"));
			fuelRec.setUnitPrice((String) entity.getProperty("unitprice"));
			fuelRec.setUserName((String) entity.getProperty("userName"));
			fuelList.add(fuelRec);
		}
		return fuelList;
	}

}
