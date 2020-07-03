package com.example.redisquickstart.mapper;

import com.example.redisquickstart.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EmployeeMapper {
    List<Employee> selectAll();

    int insert(@Param("employee") Employee employee);

    int deleteById(@Param("id") int id);

    Employee selectById(@Param("id") int id);

    int update(@Param("id") Integer id, @Param("phoneNumber") String phoneNumber);
}
