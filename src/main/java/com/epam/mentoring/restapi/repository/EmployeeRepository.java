package com.epam.mentoring.restapi.repository;

import com.epam.mentoring.restapi.modal.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pengfrancis on 16/5/19.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query(value = "SELECT * FROM Employee e WHERE e.name=?1", nativeQuery = true)
    List<Employee> findByName(String name);
}
