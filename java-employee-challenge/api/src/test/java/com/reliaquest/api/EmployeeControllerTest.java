package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.reliaquest.api.controller.impl.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.ResponseMapper;
import com.reliaquest.api.service.impl.EmployeeService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)

public class EmployeeControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    public EmployeeService employeeService;

    @Mock
    private List<Employee> employeeList;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private Employee employee;
    
    private String id = "a33d62a5-7816-44d8-b2b2-9c4667917fb5";

    @BeforeEach
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	employee =  new Employee(UUID.fromString("a33d62a5-7516-44d8-b2b2-9c4667917fb5"), "Vishal",102033,35, "Developer", "alpha@company.com");
        employeeList = Arrays.asList(
                new Employee(UUID.fromString("a33d62a5-7816-44d8-b2b2-9c4667917fb5"), "Ashish",102033,35, "Developer", "alpha@company.com"),
                new Employee(UUID.fromString("f49bdc48-96d3-4584-b0af-60883e0fd29b"), "Vikas",102033,36, "Manager","alpha@company.com"),
                		 new Employee(UUID.fromString("a33d62a5-7516-64d8-b2b2-9c4667917fb5"), "Ashish",102133, 39, "Sr Developer", "delta@company.com"));
    }
    
    @Test
    public void testGetAllEmployees_Success() throws Exception {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(employeeList);
        List<Employee> result = employeeService.getAllEmployeesWithRetry();
        assertEquals(3, result.size());
        assertEquals("Ashish", result.get(0).getEmployee_name());
        assertEquals("Vikas", result.get(1).getEmployee_name());
    }
    
    @Test
    void testGetEmployeeById_Success() throws Exception {
         when(employeeService.getExistingEmployeeById("UUID")).thenReturn(employee);
         Employee result = employeeService.getExistingEmployeeById("UUID");
        // Assert: Verify that the result matches the expected
        assertEquals(UUID.fromString("a33d62a5-7516-44d8-b2b2-9c4667917fb5"), result.getId());
        assertEquals("Vishal", result.getEmployee_name());
        assertEquals("Developer", result.getEmployee_title());

    }    
    
    @Test
    void testGetEmployeeById_Failure() throws Exception {
         when(employeeService.getExistingEmployeeById("DDUI")).thenReturn(new Employee());
        Employee result = employeeService.getExistingEmployeeById("DDUI");
        // Assert: Verify that the result matches the expected
        assertEquals(null, result.getEmployee_name());
        assertEquals(null, result.getEmployee_title());

    }
    
    // Test case X (search with Name) : Test when the employee list is empty.
    @Test
    public void testGetEmployeesByNameSearch_EmptyList() {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(new ArrayList<>());  // Mock empty list
        List<Employee> result = employeeService.getEmployeesByNameSearch("Mac");
        assertTrue(result.isEmpty()); 
    }

    // Test case XI: Test when no employee matches the search string.
    @Test
    public void testGetEmployeesByNameSearch_NoMatch() {

        when(employeeService.getAllEmployeesWithRetry()).thenReturn(employeeList);

        List<Employee> result = employeeService.getEmployeesByNameSearch("John");

        assertTrue(result.isEmpty());  // No employee name contains "John"
    }

    // Test case XII: Test when some employees match the search string.
    @Test
    public void testGetEmployeesByNameSearch_SomeMatches() {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(employeeList);
        List<Employee> result = employeeService.getEmployeesByNameSearch("Vikas");
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(emp -> emp.getEmployee_name().contains("Vikas")));
    }

    // Test case XIV: Test when the search string is null.
    @Test
    public void testGetEmployeesByNameSearch_NullSearchString() {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(employeeList);
        List<Employee> result = employeeService.getEmployeesByNameSearch(null);
        assertTrue(result.isEmpty());
    }

    // Test case XV: Test when the search string is empty.
    @Test
    public void testGetEmployeesByNameSearch_EmptySearchString() {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(employeeList);
        List<Employee> result = employeeService.getEmployeesByNameSearch("");
        assertEquals(0, result.size()); 
    }

    // Test case XVI: Test when the employee list is null.
    @Test
    public void testGetEmployeesByNameSearch_NullList() {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(null); 
        List<Employee> result = employeeService.getEmployeesByNameSearch("Actor");
        assertTrue(result.isEmpty()); 
    }
    
    @Test
    public void testGetExistingEmployeeById_Found() throws Exception {
        String apiUrl = "http://localhost:8112/api/v1/employee/" + id;
        ResponseMapper<Employee> mockResponse = new ResponseMapper<>();
        mockResponse.setData(employee);

        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(HttpEntity.class), 
                any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        Employee result = employeeService.getExistingEmployeeById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Vishal", result.getEmployee_name());
    }
    
   
}
