package com.example.redisquickstart.service;

import com.example.redisquickstart.entity.Employee;

import java.util.List;

public interface IEmployeeService {
    List<Employee> list();

    boolean save(Employee employee);

    boolean removeById(int id);

    Employee listById(int id);

    boolean edit(Integer id, String phoneNumber);
}
