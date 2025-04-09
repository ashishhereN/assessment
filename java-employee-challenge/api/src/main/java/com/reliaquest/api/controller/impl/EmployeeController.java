package com.reliaquest.api.controller.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeRequest;
import com.reliaquest.api.service.IEmployeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeRequest> {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
	
	@Autowired
	public IEmployeeService employeeService;
	
	@Override
	public ResponseEntity<List<Employee>> getAllEmployees() {
		logger.debug("Method :: getAllEmployees starts");
		List<Employee> empList = employeeService.getAllEmployeesWithRetry();
		return new ResponseEntity<List<Employee>>(empList, HttpStatus.OK);
	}

	@Override
	@GetMapping("/search/{searchString}")
	public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString) {
		logger.debug("Method :: getEmployeesByNameSearch starts");
		return new ResponseEntity<List<Employee>>(employeeService.getEmployeesByNameSearch(searchString), HttpStatus.OK);
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") String id) {
		logger.debug("Method :: getEmployeeById starts");
		logger.debug("Getting emloyee with id - "+id);
		try {
			Employee emp = employeeService.getExistingEmployeeById(id);
			return new ResponseEntity<Employee>(emp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Employee>(new Employee(), HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
		logger.debug("Method :: getHighestSalaryOfEmployees starts");
		List<Employee> empList = employeeService.getAllEmployeesWithRetry();
		Optional<Employee> highest = empList.stream().max(Comparator.comparing(Employee :: getEmployee_salary));
		Integer maxSal = !highest.isEmpty() ? highest.get().getEmployee_salary() : 0;
		logger.debug("Maximum salary is :: " + maxSal);
		return new ResponseEntity<Integer>(maxSal, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
		logger.debug("Method :: getHighestSalaryOfEmployees starts");
		return new ResponseEntity<List<String>>(employeeService.getTopTenHighestEarningEmployeeNames(), HttpStatus.OK);
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
		logger.info("Method :: deleteEmployeeById starts");
		logger.debug("Deleting employee with id " + id);
		Employee emp;
		try {
			emp = employeeService.deleteEmployee(id);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
		}
		if (emp == null) {
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<String>(emp.getEmployee_name(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Employee> createEmployee(EmployeeRequest employeeInput) {
		logger.debug("Method :: createEmployee starts");
		Employee emp = employeeService.saveEmployee(employeeInput);
		return new ResponseEntity<Employee>(emp, HttpStatus.CREATED);
	}
	
}
