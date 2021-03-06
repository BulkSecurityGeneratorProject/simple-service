package com.pure.service.service;

import com.pure.service.domain.Student;
import com.pure.service.domain.StudentClass;
import com.pure.service.service.dto.dto.CommonResponse;
import com.pure.service.service.dto.request.SingleStudentClassRequest;
import com.pure.service.service.dto.request.StudentsClassRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing StudentClass.
 */
public interface StudentClassService {

    /**
     * Save a studentClass.
     *
     * @param studentClass the entity to save
     * @return the persisted entity
     */
    StudentClass save(StudentClass studentClass);

    /**
     *  Get all the studentClasses.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<StudentClass> findAll(Pageable pageable);

    /**
     *  Get the "id" studentClass.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    StudentClass findOne(Long id);

    /**
     *  Delete the "id" studentClass.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    CommonResponse batchAssign(StudentsClassRequest studentsClassRequest);

    List<Student> findStudentsInClass(Long classId);

    StudentClass singleAssign(SingleStudentClassRequest studentsClassRequest);

    void removeStudentFromClass(Long studentId, Long classId);
}
