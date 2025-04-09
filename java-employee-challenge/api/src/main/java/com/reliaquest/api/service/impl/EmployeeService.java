package com.reliaquest.api.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.ResponseMapper;
import com.reliaquest.api.request.EmployeeRequest;
import com.reliaquest.api.service.IEmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
	
	@Value("${mock.application.host:''}")
	private String apiBaseUrl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	@Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000)) /// By pass the Ratelimiter
	public Employee getExistingEmployeeById(String id) throws Exception {
		String updateUrl = apiBaseUrl+"/"+id;
		ResponseMapper<Employee> emp = restTemplate.exchange(updateUrl, HttpMethod.GET, createGetHeaders(),
				new ParameterizedTypeReference<ResponseMapper<Employee>>() {
				}).getBody();
		if (emp.getData() == null) {
			throw new Exception("Employee not found");
		}
		return emp.getData();
	}
	
	@Override
	@Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000)) /// By pass the Ratelimiter
	public List<Employee> getAllEmployeesWithRetry() {
		ResponseMapper<List<Employee>> empList = restTemplate.exchange(apiBaseUrl, HttpMethod.GET, createGetHeaders(),
				new ParameterizedTypeReference<ResponseMapper<List<Employee>>>() {
				}).getBody();
		return empList.getData();
	}
	
	@Override
	@Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000)) /// By pass the Ratelimiter
	public Employee saveEmployee(EmployeeRequest employeeInput) {
		logger.info("IN saveEmployee Method");
		ResponseMapper<Employee> emp = restTemplate.exchange(apiBaseUrl, HttpMethod.POST, createBodyAndHeaders(employeeInput), new ParameterizedTypeReference<ResponseMapper<Employee>>() {
		}).getBody();
		return emp.getData();
	}
	
	@Override
	@Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000)) /// By pass the Ratelimiter
	public Employee deleteEmployee(String id) throws Exception {
		logger.info("IN deleteEmployee Method");
		Employee emp = getExistingEmployeeById(id);
		EmployeeRequest empRequest = new EmployeeRequest(emp);
		ResponseEntity<Employee> delEmp  = restTemplate.exchange(apiBaseUrl,HttpMethod.DELETE, createBodyAndHeaders(empRequest), Employee.class);
		return delEmp.getBody();
	}
	
	private HttpEntity<String> createGetHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<String>(headers);
	}
	
	private HttpEntity<EmployeeRequest> createBodyAndHeaders(EmployeeRequest emp) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return new HttpEntity<EmployeeRequest>(emp, headers);
	}

	@Override
	public List<String> getTopTenHighestEarningEmployeeNames() {
		List<Employee> empList = getAllEmployeesWithRetry();
		return  empList.stream().sorted(Comparator.comparing(Employee :: getEmployee_salary).reversed()).limit(10).map(e -> e.getEmployee_name()).collect(Collectors.toList());
	}

	@Override
	public List<Employee> getEmployeesByNameSearch(String searchString) {
		List<Employee> empList = getAllEmployeesWithRetry();
		List<Employee> filteredList = new ArrayList<>();
		if (!org.springframework.util.CollectionUtils.isEmpty(empList)) {
			filteredList = empList.stream().filter(emp -> emp.getEmployee_name().contains(searchString)).collect(Collectors.toList());
		}
		return filteredList;
	}
	
}
