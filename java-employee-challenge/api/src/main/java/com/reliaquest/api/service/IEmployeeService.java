package com.reliaquest.api.service;

import java.util.List;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeRequest;

public interface IEmployeeService {

	Employee getExistingEmployeeById(String id) throws Exception;
	
	List<Employee> getAllEmployeesWithRetry();
	
	List<String> getTopTenHighestEarningEmployeeNames();
	
	Employee deleteEmployee(String id) throws Exception;
	
	Employee saveEmployee(EmployeeRequest employeeInput);
	
	List<Employee> getEmployeesByNameSearch(String searchString);
	
}
