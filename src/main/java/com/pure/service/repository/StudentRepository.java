package com.pure.service.repository;

import com.pure.service.domain.Student;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Student entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    List<Student> findByCustomer_Id(Long id);

    List<Student> findByNameAndPhone(String name, String phone);

}
