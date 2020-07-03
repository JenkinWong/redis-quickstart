package com.example.redisquickstart.controller;

import com.example.redisquickstart.entity.Employee;
import com.example.redisquickstart.service.IEmployeeService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> list() {
        return employeeService.list();
    }

    @GetMapping("/{id}")
    public Employee listById(@PathVariable String id) {
        return employeeService.listById(Integer.parseInt(id));
    }

    @PostMapping
    public String save(Employee employee) {
        employee.setGmtCreate(LocalDateTime.now());
        employee.setGmtModified(LocalDateTime.now());
        boolean flag = employeeService.save(employee);

        return flag ? "OK" : "Bad request";
    }

    @PutMapping
    public Employee edit(Integer id, String phoneNumber) {
        boolean flag = employeeService.edit(id, phoneNumber);
        Employee employee = employeeService.listById(id);

        return flag ? employee : null;
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable String id) {
        boolean flag = employeeService.removeById(Integer.parseInt(id));

        return flag ? "OK" : "Bad request";
    }
}
