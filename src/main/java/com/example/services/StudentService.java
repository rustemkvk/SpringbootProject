package com.example.services;

import com.example.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentDTO saveStudent(StudentDTO studentDTO);

    List<StudentDTO> getAllStudents();

    Page<StudentDTO> getAllStudents(Pageable pageable);

    Optional<StudentDTO> getStudentById(Long id);

    StudentDTO updateStudent(Long id, StudentDTO studentDTO);

    void deleteStudent(Long id);

    Optional<StudentDTO> findByEmail(String email);
}
