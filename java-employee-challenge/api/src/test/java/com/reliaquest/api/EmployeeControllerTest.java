package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.reliaquest.api.controller.impl.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.impl.EmployeeService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    public EmployeeService employeeService;

    private List<Employee> employeeList;
    
    private Employee employee;

    @BeforeEach
    public void setup() {
    	employee =  new Employee(UUID.fromString("a33d62a5-7516-44d8-b2b2-9c4667917fb5"), "Vishal G",102033,35, "Developer", "alpha@company.com");
        employeeList = Arrays.asList(
                new Employee(UUID.fromString("a33d62a5-7516-44d8-b2b2-9c4667917fb5"), "Ashish D",102033,35, "Developer", "alpha@company.com"),
                new Employee(UUID.fromString("f49bdc48-96d3-4584-b0af-60883e0fd29b"), "Vikas K",102033,36, "Manager","alpha@company.com")
        );
    }
    
    @Test
    public void testGetAllEmployees_Success() throws Exception {
        when(employeeService.getAllEmployeesWithRetry()).thenReturn(employeeList);
        List<Employee> result = employeeService.getAllEmployeesWithRetry();
        assertEquals(2, result.size());
        assertEquals("Ashish D", result.get(0).getEmployee_name());
        assertEquals("Vikas K", result.get(1).getEmployee_name());
    }
    
    @Test
    void testGetEmployeeById_Success() throws Exception {
         when(employeeService.getExistingEmployeeById("UUID")).thenReturn(employee);
         Employee result = employeeService.getExistingEmployeeById("UUID");
        // Assert: Verify that the result matches the expected
        assertEquals(UUID.fromString("a33d62a5-7516-44d8-b2b2-9c4667917fb5"), result.getId());
        assertEquals("Vishal G", result.getEmployee_name());
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
   
}
