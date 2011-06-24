package com.ivy.auto.expense.server;

import java.util.List;

import com.ivy.auto.expense.client.FuelExpense;

public interface FuelExpenseDAO {
	void addExpense(FuelExpense fuelExpense);
	List<FuelExpense> getAllExpenses(String inpUser);
}
