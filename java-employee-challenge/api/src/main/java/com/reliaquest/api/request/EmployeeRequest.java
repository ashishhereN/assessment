package com.reliaquest.api.request;

import java.util.UUID;

import com.reliaquest.api.model.Employee;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@RequiredArgsConstructor()
public class EmployeeRequest {

	private UUID id;
	@Nonnull
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;
    
    public EmployeeRequest(Employee emp) {
    	this.id = emp.getId();
    	this.salary = emp.getEmployee_salary();
    	this.name = emp.getEmployee_name();
        this.age = emp.getEmployee_age();
        this.title = emp.getEmployee_title();
        this.email = emp.getEmployee_email();
    }
}
