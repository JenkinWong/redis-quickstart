package com.example.redisquickstart.service.impl;

import com.example.redisquickstart.entity.Employee;
import com.example.redisquickstart.mapper.EmployeeMapper;
import com.example.redisquickstart.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IEmployeeServiceImpl implements IEmployeeService, CommandLineRunner {

    private static final String REDIS_KEY_PREFIX = "employee:id:";
    private static final int TIMEOUT = 86400;

    private final EmployeeMapper employeeMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public IEmployeeServiceImpl(EmployeeMapper employeeMapper,
                                RedisTemplate<String, Object> redisTemplate) {
        this.employeeMapper = employeeMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Employee> list() {
        try {
            List<Employee> list = new ArrayList<>();
            Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    Object o = redisTemplate.opsForValue().get(key);
                    list.add((Employee) o);
                }
                return list.stream().sorted(Comparator.comparing(Employee::getId)).collect(Collectors.toList());
            } else {
                List<Employee> employees = employeeMapper.selectAll();
                for (Employee employee : employees) {
                    String key = REDIS_KEY_PREFIX + employee.getId();
                    redisTemplate.opsForValue().set(key, employee);
                }
                return employees;
            }
        } catch (Exception e) {
            log.error("{} 执行缓存操作失败", e.getMessage());
            return employeeMapper.selectAll();
        }
    }

    @Override
    public boolean save(Employee employee) {
        int i = employeeMapper.insert(employee);

        if (i == 1) {
            String key = REDIS_KEY_PREFIX + employee.getId();
            redisTemplate.opsForValue().set(key, employee, TIMEOUT, TimeUnit.SECONDS);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeById(int id) {
        int i = employeeMapper.deleteById(id);

        if (i == 1) {
            redisTemplate.delete(REDIS_KEY_PREFIX + id);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public Employee listById(int id) {
        try {
            String key = REDIS_KEY_PREFIX + id;
            Object o = redisTemplate.opsForValue().get(key);

            if (o != null) {
                return (Employee) o;
            } else {
                return employeeMapper.selectById(id);
            }
        } catch (Exception e) {
            log.error("{} 执行缓存操作失败", e.getMessage());
            return employeeMapper.selectById(id);
        }
    }

    @Override
    public boolean edit(Integer id, String phoneNumber) {
        int i = employeeMapper.update(id, phoneNumber);

        if (i == 1) {
            Employee employee = employeeMapper.selectById(id);
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + id, employee, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run(String... args) {
        try {
            List<Employee> employees = employeeMapper.selectAll();

            for (Employee employee : employees) {
                String key = REDIS_KEY_PREFIX + employee.getId();
                redisTemplate.opsForValue().set(key, employee, TIMEOUT, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("{} 执行缓存初始化失败", this.getClass().getSimpleName());
        }
    }
}
