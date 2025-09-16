package com.example.controller;

import com.example.dto.StudentDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentController {

    ResponseEntity<StudentDTO> createStudent(StudentDTO studentDTO);

    ResponseEntity<List<StudentDTO>> getAllStudents();

    ResponseEntity<StudentDTO> getStudentById(Long id);

    ResponseEntity<Void> deleteStudent(Long id);

    ResponseEntity<StudentDTO> updateStudent(Long id, StudentDTO student);
}